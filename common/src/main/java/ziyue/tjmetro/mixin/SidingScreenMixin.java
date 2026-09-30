package ziyue.tjmetro.mixin;

import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.client.TrainClientRegistry;
import mtr.client.TrainProperties;
import mtr.data.Depot;
import mtr.data.IGui;
import mtr.data.Siding;
import mtr.data.TrainType;
import mtr.data.TransportMode;
import mtr.mappings.UtilitiesClient;
import mtr.packet.IPacket;
import mtr.packet.PacketTrainDataGuiClient;
import mtr.screen.DashboardScreen;
import mtr.screen.SavedRailScreenBase;
import mtr.screen.SidingScreen;
import mtr.screen.WidgetBetterCheckbox;
import mtr.screen.WidgetBetterTextField;
import mtr.screen.WidgetShorterSlider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ziyue.tjmetro.Reference;

/**
 * Port of the MTR4 "one-click copy siding settings" feature ({@code ziyue.tjmetro.mod.mixin.SidingScreenMixin})
 * adapted to the MTR3 (3.6.2.2) {@link SidingScreen}.
 * <p>
 * Adds a "Duplicate settings to other sidings" button to the siding screen. Clicking it copies the train type
 * (and thus the vehicle count), max trains, manual/unlimited mode, max manual speed, predictive braking,
 * acceleration constant and dwell time to every other siding in the same depot.
 * <p>
 * Layout matches the MTR4 original: the button sits at {@code SQUARE_SIZE + 25} with width
 * {@code width - textWidth - SQUARE_SIZE * 2}, and the 20x20 {@code tianjin_metro_mod_logo.png} texture is
 * drawn at {@code SQUARE_SIZE} on the same row, to the left of the button.
 * <p>
 * The button position/visibility is refreshed from the {@code shouldRenderExtra()} hook, which
 * {@code SavedRailScreenBase} calls once from {@code tick()} and once from {@code render()} every frame (the
 * same hook the feature already used). The logo is drawn from the tail of {@code render()}, i.e. strictly
 * during the render pass - see {@link #afterRender} for why three mapping-dependent method names are
 * registered there.
 * <p>
 * MTR-owned members ({@code <init>}, {@code shouldRenderExtra()}) are targeted with {@code remap = false} on
 * both loaders, matching the other registered mixins in this project.
 *
 * @author ZiYueCommentary
 * @see SidingScreen
 * @since 1.0.2
 */

@Mixin(SidingScreen.class)
public abstract class SidingScreenMixin extends SavedRailScreenBase<Siding> implements IGui
{
	@Shadow(remap = false)
	@Final
	private WidgetBetterTextField textFieldMaxTrains;

	@Shadow(remap = false)
	@Final
	private WidgetBetterTextField textFieldAcceleration;

	@Shadow(remap = false)
	@Final
	private WidgetBetterCheckbox buttonUnlimitedTrains;

	@Shadow(remap = false)
	@Final
	private WidgetBetterCheckbox buttonIsManual;

	@Shadow(remap = false)
	@Final
	private WidgetBetterCheckbox buttonPredictiveBraking;

	@Shadow(remap = false)
	@Final
	private WidgetShorterSlider sliderMaxManualSpeed;

	@Shadow(remap = false)
	private boolean isSelectingTrain;

	@Shadow(remap = false)
	@Final
	private static float ACCELERATION_UNIT_CONVERSION_1;

	public SidingScreenMixin(Siding savedRailBase, TransportMode transportMode, DashboardScreen dashboardScreen) {
		super(savedRailBase, transportMode, dashboardScreen);
	}

	@Unique
	private final Button buttonDuplicateSettings = UtilitiesClient.newButton(button -> duplicateSettings());

	@Unique
	private final ResourceLocation duplicateSettingsLogoResource = new ResourceLocation(Reference.MOD_ID, "textures/sign/tianjin_metro_mod_logo.png");

	@Unique
	private boolean buttonDuplicateSettingsAdded;

	@Inject(at = @At("TAIL"), method = "<init>", remap = false)
	private void afterConstruct(Siding siding, TransportMode transportMode, DashboardScreen dashboardScreen, CallbackInfo ci) {
		buttonDuplicateSettings.setMessage(Component.translatable("button.tjmetro.duplicate_siding_settings"));
	}

	@Inject(at = @At("TAIL"), method = "shouldRenderExtra", remap = false)
	private void afterShouldRenderExtra(CallbackInfoReturnable<Boolean> ci) {
		if (showScheduleControls && !buttonDuplicateSettingsAdded) {
			addDrawableChild(buttonDuplicateSettings);
			buttonDuplicateSettingsAdded = true;
		}
		buttonDuplicateSettings.visible = showScheduleControls && !isSelectingTrain;
		IDrawing.setPositionAndWidth(buttonDuplicateSettings, SQUARE_SIZE + 25, duplicateSettingsButtonY(), width - textWidth - SQUARE_SIZE * 2);
	}

	/**
	 * Draws the mod logo to the left of the button, mirroring the MTR4 original, which draws it at the tail of
	 * {@code SidingScreen.render}.
	 * <p>
	 * {@code render} is a Minecraft override, so its name depends on the mapping set in use, and the method
	 * reference of an {@code @Inject} is a string constant that remapJar does not rewrite. All three names are
	 * therefore registered with {@code require = 0}, and only the one matching the running platform applies:
	 * {@code render} (named dev environment and Forge dev), {@code method_25394} (Fabric production, which uses
	 * intermediary names) and {@code m_88315_} (Forge production, which uses SRG names up to 1.20.1). This is the
	 * same split the project already uses for {@code RenderTrainsMixin} / {@code RenderTrainsFabricMixin}.
	 */
	@Inject(at = @At("TAIL"), method = "render", remap = false, require = 0)
	private void afterRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		renderDuplicateSettingsLogo(guiGraphics);
	}

	@Inject(at = @At("TAIL"), method = "method_25394", remap = false, require = 0)
	private void afterRenderIntermediary(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		renderDuplicateSettingsLogo(guiGraphics);
	}

	@Inject(at = @At("TAIL"), method = "m_88315_", remap = false, require = 0)
	private void afterRenderSrg(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		renderDuplicateSettingsLogo(guiGraphics);
	}

	@Unique
	private void renderDuplicateSettingsLogo(GuiGraphics guiGraphics) {
		if (!showScheduleControls || isSelectingTrain) {
			return;
		}
		UtilitiesClient.beginDrawingTexture(duplicateSettingsLogoResource);
		guiGraphics.blit(duplicateSettingsLogoResource, SQUARE_SIZE, duplicateSettingsButtonY(), 0, 0, 20, 20, 20, 20);
	}

	/**
	 * Y of both the button and the logo.
	 * <p>
	 * The MTR4 original uses
	 * {@code SQUARE_SIZE * (buttonIsManual.isChecked2() ? 13 : 10) + TEXT_FIELD_PADDING * 2 + TEXT_PADDING},
	 * because the MTR4 siding screen re-flows when manual driving is toggled and frees/occupies three rows. The
	 * MTR3 siding screen has a static layout - every widget Y is a hard-coded constant in {@code SidingScreen.init}
	 * ({@code m_7856_}) and nothing repositions them when {@code buttonIsManual} is toggled - so the conditional
	 * branch has no counterpart here and is dropped. Row 10 is the only correct row; row 13 (= 260) would put the
	 * button past the bottom of the screen on the common GUI scales.
	 */
	@Unique
	private int duplicateSettingsButtonY() {
		return SQUARE_SIZE * 10 + TEXT_FIELD_PADDING * 2 + TEXT_PADDING;
	}

	@Unique
	private void duplicateSettings() {
		final Depot depot = ClientData.DATA_CACHE.sidingIdToDepot.get(savedRailBase.id);
		if (depot == null) {
			return;
		}

		final String trainId = savedRailBase.getTrainId();
		final TrainProperties trainProperties = trainId.isEmpty() ? null : TrainClientRegistry.getTrainProperties(trainId);

		ClientData.DATA_CACHE.requestDepotIdToSidings(depot.id).values().forEach(siding -> {
			if (trainProperties != null && siding.isValidVehicle(TrainType.getSpacing(trainProperties.baseTrainType))) {
				siding.setTrainIdAndBaseType(trainId, trainProperties.baseTrainType, packet -> PacketTrainDataGuiClient.sendUpdate(IPacket.PACKET_UPDATE_SIDING, packet));
			}

			int maxTrains;
			try {
				maxTrains = Math.max(0, Integer.parseInt(textFieldMaxTrains.getValue()) - 1);
			} catch (Exception ignored) {
				maxTrains = 0;
			}

			float accelerationConstant;
			try {
				final float valueMps2 = Float.parseFloat(textFieldAcceleration.getValue());
				final double valueMptick2 = valueMps2 / ACCELERATION_UNIT_CONVERSION_1;
				final double clamped = Mth.clamp(valueMptick2, 1.0E-4F, 0.05F);
				final long factor = (long) Math.pow(10.0, 5.0);
				accelerationConstant = (float) (Math.round(clamped * factor) / (double) factor);
			} catch (Exception ignored) {
				accelerationConstant = 0.01F;
			}

			siding.setUnlimitedTrains(
					buttonUnlimitedTrains.selected(),
					maxTrains,
					buttonIsManual.selected(),
					sliderMaxManualSpeed.getIntValue(),
					buttonPredictiveBraking.selected(),
					accelerationConstant,
					savedRailBase.getDwellTime(),
					true,
					packet -> PacketTrainDataGuiClient.sendUpdate(IPacket.PACKET_UPDATE_SIDING, packet)
			);
		});

		this.onClose();
	}
}
