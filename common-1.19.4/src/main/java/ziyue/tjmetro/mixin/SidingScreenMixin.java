package ziyue.tjmetro.mixin;

import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.client.TrainClientRegistry;
import mtr.client.TrainProperties;
import mtr.data.Depot;
import mtr.data.IGui;
import mtr.data.RailwayData;
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
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Port of the MTR4 "one-click copy siding settings" feature ({@code ziyue.tjmetro.mod.mixin.SidingScreenMixin})
 * adapted to the MTR3 (3.2.2) {@link SidingScreen}.
 * <p>
 * Adds a "Duplicate settings to other sidings" button to the siding screen. Clicking it copies the train type
 * (and thus the vehicle count), max trains, manual/unlimited mode, max manual speed, acceleration constant
 * and dwell time to every other siding in the same depot.
 * <p>
 * Only MTR-owned members are targeted (safe with {@code remap = false} on both loaders, matching the other
 * registered mixins in this project): the constructor {@code <init>} and the per-frame hook
 * {@code shouldRenderExtra()}, which {@code SavedRailScreenBase} calls from both {@code tick()} and
 * {@code render()} every frame.
 * <p>
 * MTR 3.2.2 differences from the 3.6.2.2 original: the acceleration is edited with the
 * {@code sliderAccelerationConstant} slider instead of a text field, there is no predictive-braking
 * checkbox, and {@code Siding.setUnlimitedTrains} takes 8 arguments (no predictive-braking flag).
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
	private WidgetShorterSlider sliderAccelerationConstant;

	@Shadow(remap = false)
	@Final
	private WidgetBetterCheckbox buttonUnlimitedTrains;

	@Shadow(remap = false)
	@Final
	private WidgetBetterCheckbox buttonIsManual;

	@Shadow(remap = false)
	@Final
	private WidgetShorterSlider sliderMaxManualSpeed;

	@Shadow(remap = false)
	private boolean isSelectingTrain;

	public SidingScreenMixin(Siding savedRailBase, TransportMode transportMode, DashboardScreen dashboardScreen) {
		super(savedRailBase, transportMode, dashboardScreen);
	}

	@Unique
	private final Button buttonDuplicateSettings = UtilitiesClient.newButton(button -> duplicateSettings());

	@Unique
	private boolean buttonDuplicateSettingsAdded;

	@Inject(at = @At("TAIL"), method = "<init>", remap = false)
	private void afterConstruct(Siding siding, TransportMode transportMode, DashboardScreen dashboardScreen, CallbackInfo ci) {
		buttonDuplicateSettings.setMessage(Component.translatable("button.tjmetro.duplicate_siding_settings"));
	}

	@Inject(at = @At("TAIL"), method = "shouldRenderExtra", remap = false)
	private void afterShouldRenderExtra(CallbackInfo ci) {
		if (showScheduleControls && !buttonDuplicateSettingsAdded) {
			addDrawableChild(buttonDuplicateSettings);
			buttonDuplicateSettingsAdded = true;
		}
		buttonDuplicateSettings.visible = showScheduleControls && !isSelectingTrain;
		IDrawing.setPositionAndWidth(buttonDuplicateSettings, SQUARE_SIZE + textWidth, SQUARE_SIZE * 10 - 2, width - textWidth - SQUARE_SIZE * 2);
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
				final float valueMptick2 = Mth.clamp(sliderAccelerationConstant.getIntValue() / 1000F + 0.001F, 0.001F, 0.05F);
				accelerationConstant = RailwayData.round(valueMptick2, 3);
			} catch (Exception ignored) {
				accelerationConstant = 0.01F;
			}

			siding.setUnlimitedTrains(
					buttonUnlimitedTrains.selected(),
					maxTrains,
					buttonIsManual.selected(),
					sliderMaxManualSpeed.getIntValue(),
					accelerationConstant,
					savedRailBase.getDwellTime(),
					true,
					packet -> PacketTrainDataGuiClient.sendUpdate(IPacket.PACKET_UPDATE_SIDING, packet)
			);
		});

		this.onClose();
	}
}
