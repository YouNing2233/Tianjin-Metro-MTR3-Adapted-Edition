# 生成 tjmetro 的 ja_jp / fr_fr / zh_tw 语言文件。
#
# 策略：
#   1. 以上游（ZiYueCommentary/Tianjin-Metro）已提供的翻译为基底（与上游保持一致）；
#   2. 我们移植版新增的键（出口换乘、单格指示牌、安检牌、导向标志、新配置项等）按
#      规则从 en_us / zh_cn 生成，长句在下方 Curated 表里手工给出；
#   3. 键集合始终与 en_us 完全一致（不遗漏、不多余）。
#
# 用法：pwsh -File tools/gen-lang.ps1
# 上游语言文件需先下载到 build/upstream-lang/（ja_jp fr_fr zh_tw zh_hk）。

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$langDirs = @(
    'common', 'common-1.16.5', 'common-1.18.2', 'common-1.19.2', 'common-1.19.4', 'common-1.21.1'
) | ForEach-Object { Join-Path $root "$_\src\main\resources\assets\tjmetro\lang" }
$enPath = Join-Path $root 'common\src\main\resources\assets\tjmetro\lang\en_us.json'
$zhPath = Join-Path $root 'common\src\main\resources\assets\tjmetro\lang\zh_cn.json'
$upDir = Join-Path $root 'build\upstream-lang'

function Read-Json([string]$path) {
    return ([IO.File]::ReadAllText($path, [Text.Encoding]::UTF8) | ConvertFrom-Json)
}

$en = Read-Json $enPath
$zh = Read-Json $zhPath
$keys = $en.PSObject.Properties.Name
$enMap = @{}
$en.PSObject.Properties | ForEach-Object { $enMap[$_.Name] = [string]$_.Value }
$zhMap = @{}
$zh.PSObject.Properties | ForEach-Object { $zhMap[$_.Name] = [string]$_.Value }

function Load-Upstream([string]$name) {
    $p = Join-Path $upDir $name
    if (-not (Test-Path $p)) { return @{} }
    $o = Read-Json $p
    $m = @{}
    $o.PSObject.Properties | ForEach-Object { $m[$_.Name] = [string]$_.Value }
    return $m
}

# ---------------------------------------------------------------- 规则：en_us -> ja_jp
$jaRules = @(
    @('^Tianjin Metro$', '天津メトロ'),
    @('\(Tianjin, Line 11\)', '（天津・11号線）'),
    @('\(Tianjin, Line 9\)', '（天津・9号線）'),
    @('\(Tianjin, Line 1\)', '（天津・1号線）'),
    @('\(Tianjin, Jinjing Line\)', '（天津・津静線）'),
    @('\(Tianjin\)', '（天津）'),
    @('\(Line 11\)', '（11号線）'),
    @('\(Line 9\)', '（9号線）'),
    @('\(Line 1\)', '（1号線）'),
    @('\(Jinjing Line\)', '（津静線）'),
    @('\(Single, Left\)', '（片開き・左）'),
    @('\(Single, Right\)', '（片開き・右）'),
    @('\(Not lit\)', '（消灯）'),
    @('Automatic Platform Gates Glass End', '可動式ホーム柵ガラス端部'),
    @('Automatic Platform Gates Glass', '可動式ホーム柵ガラス'),
    @('Automatic Platform Gates', '可動式ホーム柵'),
    @('Platform Screen Doors', 'ホームドア'),
    @('Platform Slab', 'ホームハーフブロック'),
    @('Platform', 'ホーム'),
    @('Glass End', 'ガラス端部'),
    @('Glass', 'ガラス'),
    @('Top\b', '上部'),
    @('Ticket Barrier', '改札機'),
    @('\(Entrance', '（入場'),
    @('\(Exit', '（出場'),
    @('Outbound Transfer', '乗り換え改札'),
    @('Station Name Sign', '駅名標'),
    @('Station Name Projector', '駅名プロジェクター'),
    @('Station Name \(Entrance', '駅名（出入口'),
    @('Station Name \(Wall, Legacy\)', '駅名（壁掛け・旧型）'),
    @('Station Name Plate', '駅名標'),
    @('Railway Sign Pole', '案内標識の柱'),
    @('Railway Sign', '案内標識'),
    @('Route Map', '路線図'),
    @('Wall', '壁掛け'),
    @('Double', '両面'),
    @('Big', '大型'),
    @('Ceiling', '天井'),
    @('Gray Marble', '灰色大理石'),
    @('Yellow Marble', '黄色大理石'),
    @('Marble', '大理石'),
    @('Mosaic Tile', 'モザイクタイル'),
    @('Concrete', 'コンクリート'),
    @('Slab', 'ハーフブロック'),
    @('Stairs', '階段'),
    @('Single', '片開き'),
    @('APG Corner', 'ホーム柵コーナー'),
    @('Bench', 'ベンチ'),
    @('Trash Can', 'ゴミ箱'),
    @('Smoke Alarm', '煙感知器'),
    @('Player Detector', 'プレイヤー検知器'),
    @('High Speed Repeater', '高速リピーター'),
    @('Time Display \(Even\)', '時刻表示（偶数）'),
    @('Time Display', '時刻表示'),
    @('Emergency Exit Sign', '非常口標識'),
    @('Service Corridor', '業務通路'),
    @('Metal Detection Door', '金属探知ゲート'),
    @('Station Navigator Pole', '駅案内標の柱'),
    @('Station Navigator', '駅案内標'),
    @('Rolling shutter', 'シャッター'),
    @('Tianjin Metro Logo', '天津メトロのロゴ'),
    @('Logo', 'ロゴ'),
    @('Security Check', '手荷物検査'),
    @('Passenger Information Display System', '旅客案内表示装置'),
    @('Miscellaneous', 'その他'),
    @('Buildings', '建築'),
    @('Station Name Signs', '駅名標'),
    @('Platform Gates', 'ホームドア'),
    @('Decorations', '装飾'),
    @('Ceilings', '天井'),
    @('Railway Signs', '案内標識'),
    @('Dashboards', '操作パネル'),
    @('Rails', 'レール'),
    @('Signals', '信号'),
    @('Creators', '設置ツール'),
    @('Nodes', 'ノード'),
    @('Information Displays', '情報表示装置'),
    @('Fences', 'フェンス'),
    @('Route Signs', '路線標識'),
    @('Signal Lights', '信号灯'),
    @('Ticket', '切符'),
    @('Escalators', 'エスカレーター'),
    @('Lifts / Elevators', 'エレベーター'),
    @('Marbles', '大理石'),
    @('Platforms', 'ホーム'),
    @('Station Color Blocks', '駅色ブロック'),
    @('Station Color Slabs', '駅色ハーフブロック'),
    @('Uncategorized', '未分類'),
    @('Wrench', 'レンチ')
)

# ---------------------------------------------------------------- 规则：en_us -> fr_fr
$frRules = @(
    @('^Tianjin Metro$', 'Métro de Tianjin'),
    @('\(Tianjin, Line 11\)', ' (Tianjin, ligne 11)'),
    @('\(Tianjin, Line 9\)', ' (Tianjin, ligne 9)'),
    @('\(Tianjin, Line 1\)', ' (Tianjin, ligne 1)'),
    @('\(Tianjin, Jinjing Line\)', ' (Tianjin, ligne Jinjing)'),
    @('\(Tianjin\)', ' (Tianjin)'),
    @('\(Line 11\)', ' (ligne 11)'),
    @('\(Line 9\)', ' (ligne 9)'),
    @('\(Line 1\)', ' (ligne 1)'),
    @('\(Jinjing Line\)', ' (ligne Jinjing)'),
    @('\(Single, Left\)', ' (simple, gauche)'),
    @('\(Single, Right\)', ' (simple, droite)'),
    @('\(Not lit\)', ' (éteint)'),
    @('Automatic Platform Gates Glass End', 'Portes palières automatiques, verre d''extrémité'),
    @('Automatic Platform Gates Glass', 'Portes palières automatiques, verre'),
    @('Automatic Platform Gates', 'Portes palières automatiques'),
    @('Platform Screen Doors', 'Portes palières'),
    @('Platform Slab', 'Dalle de quai'),
    @('Platform', 'Quai'),
    @('Glass End', 'Verre d''extrémité'),
    @('Glass', 'Verre'),
    @('Top\b', 'Haut'),
    @('Ticket Barrier', 'Portillon'),
    @('\(Entrance', ' (entrée'),
    @('\(Exit', ' (sortie'),
    @('Outbound Transfer', 'Correspondance hors zone'),
    @('Station Name Sign', 'Panneau de nom de station'),
    @('Station Name Projector', 'Projecteur de nom de station'),
    @('Station Name \(Entrance', 'Nom de station (entrée'),
    @('Station Name \(Wall, Legacy\)', 'Nom de station (mural, ancien)'),
    @('Station Name Plate', 'Plaque de nom de station'),
    @('Railway Sign Pole', 'Mât de panneau ferroviaire'),
    @('Railway Sign', 'Panneau ferroviaire'),
    @('Route Map', 'Plan de ligne'),
    @('Wall', 'mural'),
    @('Double', 'double'),
    @('Big', 'grand'),
    @('Ceiling', 'Plafond'),
    @('Gray Marble', 'Marbre gris'),
    @('Yellow Marble', 'Marbre jaune'),
    @('Marble', 'Marbre'),
    @('Mosaic Tile', 'Mosaïque'),
    @('Concrete', 'Béton'),
    @('Slab', 'Dalle'),
    @('Stairs', 'Escalier'),
    @('Single', 'simple'),
    @('APG Corner', 'Angle de porte palière'),
    @('Bench', 'Banc'),
    @('Trash Can', 'Poubelle'),
    @('Smoke Alarm', 'Détecteur de fumée'),
    @('Player Detector', 'Détecteur de joueur'),
    @('High Speed Repeater', 'Répéteur rapide'),
    @('Time Display \(Even\)', 'Affichage de l''heure (pair)'),
    @('Time Display', 'Affichage de l''heure'),
    @('Emergency Exit Sign', 'Panneau de sortie de secours'),
    @('Service Corridor', 'Couloir de service'),
    @('Metal Detection Door', 'Portique de détection de métaux'),
    @('Station Navigator Pole', 'Mât de panneau de guidage'),
    @('Station Navigator', 'Panneau de guidage'),
    @('Rolling shutter', 'Rideau roulant'),
    @('Tianjin Metro Logo', 'Logo du métro de Tianjin'),
    @('Logo', 'Logo'),
    @('Security Check', 'Contrôle de sécurité'),
    @('Passenger Information Display System', 'Affichage d''information voyageurs'),
    @('Miscellaneous', 'Divers'),
    @('Buildings', 'Bâtiments'),
    @('Station Name Signs', 'Panneaux de nom de station'),
    @('Platform Gates', 'Portes palières'),
    @('Decorations', 'Décoration'),
    @('Ceilings', 'Plafonds'),
    @('Railway Signs', 'Panneaux ferroviaires'),
    @('Dashboards', 'Panneaux de contrôle'),
    @('Rails', 'Rails'),
    @('Signals', 'Signaux'),
    @('Creators', 'Outils de pose'),
    @('Nodes', 'Nœuds'),
    @('Information Displays', 'Affichages d''information'),
    @('Fences', 'Clôtures'),
    @('Route Signs', 'Panneaux de ligne'),
    @('Signal Lights', 'Feux de signalisation'),
    @('Ticket', 'Billet'),
    @('Escalators', 'Escaliers mécaniques'),
    @('Lifts / Elevators', 'Ascenseurs'),
    @('Marbles', 'Marbres'),
    @('Platforms', 'Quais'),
    @('Station Color Blocks', 'Blocs couleur station'),
    @('Station Color Slabs', 'Dalles couleur station'),
    @('Uncategorized', 'Non classé'),
    @('Wrench', 'Clé')
)

# ---------------------------------------------------------------- zh_cn -> zh_tw 词表
$twWords = [ordered]@{
    '站台门'='月臺門'; '站台'='月臺'; '屏蔽门'='月臺門'; '闸机'='閘機'; '入站'='進站'; '出站'='出站';
    '换乘'='轉乘'; '出站换乘'='出站轉乘'; '指示牌'='指示牌'; '路线图'='路線圖'; '站名'='站名';
    '顶部'='頂部'; '玻璃端'='玻璃端'; '玻璃'='玻璃'; '单扇'='單扇'; '左侧'='左側'; '右侧'='右側';
    '天棚'='天棚'; '天花板'='天花板'; '大理石'='大理石'; '灰色'='灰色'; '黄色'='黃色'; '台阶'='階梯';
    '楼梯'='樓梯'; '半砖'='半磚'; '石板'='石板'; '马赛克'='馬賽克'; '瓷砖'='磁磚'; '混凝土'='混凝土';
    '长凳'='長凳'; '椅子'='椅子'; '垃圾桶'='垃圾桶'; '烟雾报警器'='煙霧警報器'; '玩家探测器'='玩家探測器';
    '高速中继器'='高速中繼器'; '时间显示屏'='時間顯示器'; '紧急出口'='緊急出口'; '标识'='標識';
    '服务走廊'='服務走廊'; '金属探测门'='金屬探測門'; '导向标志'='導向標誌'; '站外导向标志'='站外導向標誌';
    '柱'='柱'; '卷帘'='捲簾'; '标志'='標誌'; '安全检查'='安全檢查'; '安检'='安檢'; '广告'='廣告';
    '筛选'='篩選'; '播放'='播放'; '实时'='即時'; '链接'='連結'; '选择'='選擇'; '设置'='設定';
    '启用'='啟用'; '禁用'='停用'; '旋转'='旋轉'; '缩小'='縮小'; '竖排'='直排'; '字号'='字級';
    '密度'='密度'; '调试'='除錯'; '选项'='選項'; '下载'='下載'; '未安装'='未安裝'; '需要'='需要';
    '游戏规则'='遊戲規則'; '范围'='範圍'; '半径'='半徑'; '阻止'='阻止'; '下落'='掉落'; '沙子'='沙子';
    '铁砧'='鐵砧'; '添加'='新增'; '获取'='取得'; '贡献者'='貢獻者'; '源码'='原始碼'; '论坛'='論壇';
    '本地化'='本地化'; '参与'='參與'; '欢迎'='歡迎'; '支持'='支持'; '赞助'='贊助'; '教程'='教學';
    '视频'='影片'; '拆弹专家'='拆彈專家'; '有趣'='有趣'; '模组'='模組'; '附属'='附屬'; '默认'='預設';
    '颜色'='顏色'; '内容'='內容'; '支持自定义'='支援自訂'; '自定义'='自訂'; '显示'='顯示';
    '下一站'='下一站'; '终点站'='終點站'; '当前站'='當前站'; '本次列车'='本班列車'; '开往'='開往';
    '方向'='方向'; '线路'='路線'; '号线'='號線'; '津静线'='津靜線'; '即将进站'='即將進站';
    '已到站'='已到站'; '分钟'='分鐘'; '秒'='秒'; '下次列車'='下一班列車';
    '客服中心'='客服中心'; '母婴室'='母嬰室'; '警务室'='警務室'; '禁止通行'='禁止通行';
    '无障碍电梯'='無障礙電梯'; '自动售票'='自動售票'; '火车站'='火車站'; '无障碍卫生间'='無障礙洗手間';
    '补票'='補票'; '问询'='詢問'; '无障碍通道'='無障礙通道'; '卫生间'='洗手間'; '乘客止步'='乘客止步';
    '乘车'='乘車'; '出口'='出口'; '售票处'='售票處'; '自助购票'='自助購票'; '请接受安检'='請接受安檢';
    '如需帮助请联系车站'='如需協助請聯繫車站'; '点击这里'='點擊這裡'; '复制'='複製'; '广告链接'='廣告連結'
}

# 简 -> 繁 字级映射（仅收无歧义的字，避免「干/乾」「里/裡」这类误伤）
$twChars = [ordered]@{
    '门'='門'; '问'='問'; '车'='車'; '铁'='鐵'; '轨'='軌'; '号'='號'; '线'='線'; '显'='顯'; '楼'='樓';
    '阶'='階'; '顶'='頂'; '单'='單'; '侧'='側'; '砖'='磚'; '墙'='牆'; '图'='圖'; '标'='標'; '灯'='燈';
    '电'='電'; '兴'='興'; '与'='與'; '无'='無'; '处'='處'; '员'='員'; '开'='開'; '关'='關'; '后'='後';
    '于'='於'; '并'='並'; '为'='為'; '请'='請'; '谢'='謝'; '帮'='幫'; '联'='聯'; '转'='轉'; '换'='換';
    '费'='費'; '输'='輸'; '检'='檢'; '测'='測'; '范'='範'; '围'='圍'; '设'='設'; '试'='試'; '调'='調';
    '选'='選'; '项'='項'; '级'='級'; '缩'='縮'; '竖'='豎'; '码'='碼'; '络'='絡'; '广'='廣'; '链'='鏈';
    '点'='點'; '击'='擊'; '这'='這'; '辆'='輛'; '厅'='廳'; '场'='場'; '队'='隊'; '观'='觀'; '览'='覽';
    '书'='書'; '买'='買'; '卖'='賣'; '货'='貨'; '贝'='貝'; '贵'='貴'; '际'='際'; '边'='邊'; '过'='過';
    '还'='還'; '进'='進'; '带'='帶'; '须'='須'; '顺'='順'; '时'='時'; '间'='間'; '对'='對'; '将'='將';
    '应'='應'; '该'='該'; '让'='讓'; '从'='從'; '众'='眾'; '会'='會'; '议'='議'; '论'='論'; '谈'='談';
    '话'='話'; '说'='說'; '读'='讀'; '写'='寫'; '录'='錄'; '视'='視'; '频'='頻'; '术'='術'; '质'='質';
    '构'='構'; '层'='層'; '库'='庫'; '统'='統'; '计'='計'; '义'='義'; '题'='題'; '类'='類'; '别'='別';
    '种'='種'; '样'='樣'; '训'='訓'; '练'='練'; '习'='習'; '难'='難'; '错'='錯'; '误'='誤'; '确'='確';
    '认'='認'; '识'='識'; '证'='證'; '据'='據'; '验'='驗'; '坏'='壞'; '废'='廢'; '弃'='棄'; '终'='終';
    '断'='斷'; '继'='繼'; '续'='續'; '现'='現'; '实'='實'; '态'='態'; '势'='勢'; '动'='動'; '静'='靜';
    '缓'='緩'; '备'='備'; '运'='運'; '营'='營'; '业'='業'; '务'='務'; '职'='職'; '岗'='崗'; '杂'='雜'
}
function Convert-ZhTw([string]$text) {
    if ([string]::IsNullOrEmpty($text)) { return $text }
    $out = $text
    foreach ($k in $twWords.Keys) { $out = $out.Replace($k, $twWords[$k]) }
    foreach ($k in $twChars.Keys) { $out = $out.Replace($k, $twChars[$k]) }
    return $out
}

function Apply-Rules([string]$text, $rules) {
    $out = $text
    foreach ($rule in $rules) {
        $out = [regex]::Replace($out, $rule[0], $rule[1])
    }
    return ($out -replace '  +', ' ').Trim()
}

# ---------------------------------------------------------------- 长句（上游没有、或需要覆盖的）
$jaCurated = @{
    'itemGroup.tjmetro.tab' = '天津メトロ'
    'itemGroup.tjmetro.tjmetro_tab' = '天津メトロ'
    'modmenu.nameTranslation.tjmetro' = '天津メトロ'
    'modmenu.descriptionTranslation.tjmetro' = 'Minecraft Transit Railway のアドオンモッド'
    'gui.tjmetro.service_corridor_sign' = '業務通路'
    'tooltip.tjmetro.railway_sign_tianjin_pole' = '案内標識または PIDS の上に設置してください。'
    'tooltip.tjmetro.not_suggest' = 'この機能は推奨されません。'
    'tooltip.tjmetro.mtr_config_screen' = '元の画面の代わりに新しい設定画面を使用します。'
    'tooltip.tjmetro.station_name_sign' = '小型の駅名標です。レンチで表示内容を変更できます。'
    'tooltip.tjmetro.disable_train_rendering' = '列車の描画を省略して性能を上げます。列車は表示されません。'
    'tooltip.tjmetro.disable_dynamic_textures' = '案内標識やホームドアなどのテクスチャ生成を省略します。'
    'config.tjmetro.station_name_size' = '駅名の文字サイズ（%）'
    'tooltip.tjmetro.station_name_size' = '路線図上の駅名の全体サイズです。100 が既定値です。駅名が密集して重なる場合は小さくしてください。「駅名の自動縮小」と併用できます。'
    'tooltip.tjmetro.shrink_dense_station_name' = '駅の多い路線では駅名を自動的に縮小し、隣り合う駅名が重ならないようにします。'
    'tooltip.tjmetro.vertical_interchange_name' = '乗り換え路線名を縦書き（1 文字ずつ改行）にし、横幅を 1 文字分に抑えて駅名と重ならないようにします。'
    'config.tjmetro.cloth_config_hint' = 'ESC で戻ります。'
    'config.tjmetro.shrink_dense_station_name' = '駅名を自動的に縮小する'
    'config.tjmetro.vertical_interchange_name' = '乗り換え路線名を縦書きにする'
    'chat.tjmetro.ad_link' = '広告リンク: %s'
    'gui.tjmetro.filter_platforms' = 'ホームを絞り込む'
    'gui.tjmetro.play_advertisements' = '広告を再生'
    'gui.tjmetro.use_real_time' = '現実の時刻を使用'
    'gui.tjmetro.select_advertisements' = '広告を選択'
    'gamerule.smokeAlarmRange' = '煙感知器の検知範囲'
    'gamerule.smokeAlarmRange.description' = '煙感知器の検知半径です。このルールは天津メトロmodによって追加されました。'
    'filter.tjmetro.mtr_station_building_blocks_station_color_slabs' = '駅色ハーフブロック'
    'sign.tjmetro.tianjin_metro_cjk' = '天津メトロ'
    'sign.tjmetro.train_cjk' = '列車'
    'sign.tjmetro.station_cjk' = '駅名'
    'sign.tjmetro.emergency_exit_cjk' = '非常口'
    'sign.tjmetro.no_throughfare_cjk' = '通り抜け禁止'
    'sign.tjmetro.accessible_elevator_cjk' = 'バリアフリーエレベーター'
    'sign.tjmetro.automatic_ticket_cjk' = '自動券売機'
    'sign.tjmetro.railway_station_cjk' = '駅'
    'sign.tjmetro.accessible_toilet_cjk' = 'バリアフリートイレ'
    'sign.tjmetro.fare_adjustment_cjk' = '運賃精算'
    'sign.tjmetro.inquiry_cjk' = 'ご案内'
    'sign.tjmetro.bound_for_cjk' = '%s方面'
    'sign.tjmetro.accessible_passage_cjk' = 'バリアフリー通路'
    'sign.tjmetro.toilet_cjk' = 'トイレ'
    'sign.tjmetro.no_entry_bmt_cjk' = '立入禁止'
    'sign.tjmetro.to_subway_bmt_cjk' = '地下鉄乗り場'
    'sign.tjmetro.exit_bmt_cjk' = '出口'
    'sign.tjmetro.tickets_bmt_cjk' = 'きっぷ売り場'
    'sign.tjmetro.security_check' = '手荷物検査'
    'gui.tjmetro.accept_security_check_cjk' = '手荷物検査を受けてください'
}

$frCurated = @{
    'modmenu.nameTranslation.tjmetro' = 'Métro de Tianjin'
    'modmenu.descriptionTranslation.tjmetro' = 'Un module complémentaire pour Minecraft Transit Railway'
    'itemGroup.tjmetro.tab' = 'Métro de Tianjin'
    'itemGroup.tjmetro.tjmetro_tab' = 'Métro de Tianjin'
    'gui.tjmetro.service_corridor_sign' = 'Couloir de service'
    'tooltip.tjmetro.railway_sign_tianjin_pole' = 'Doit être placé sur un panneau ou un PIDS.'
    'tooltip.tjmetro.not_suggest' = 'Cette fonctionnalité n''est pas recommandée.'
    'tooltip.tjmetro.mtr_config_screen' = 'Utiliser le nouvel écran de configuration à la place de l''original.'
    'tooltip.tjmetro.station_name_sign' = 'Petit panneau de nom de station ; utilisez la clé pour changer le contenu.'
    'tooltip.tjmetro.disable_train_rendering' = 'Ignore le rendu des trains pour de meilleures performances ; les trains deviennent invisibles.'
    'tooltip.tjmetro.disable_dynamic_textures' = 'Ignore la génération des textures pour les panneaux, portes, etc.'
    'config.tjmetro.station_name_size' = 'Taille des noms de station (%)'
    'tooltip.tjmetro.station_name_size' = 'Taille globale des noms de station sur les plans de ligne ; 100 est la valeur par défaut. Diminuez-la lorsque les noms se chevauchent ; se cumule avec le rétrécissement automatique.'
    'tooltip.tjmetro.shrink_dense_station_name' = 'Les noms de station rétrécissent à mesure que la ligne comporte de stations, afin d''éviter les chevauchements.'
    'tooltip.tjmetro.vertical_interchange_name' = 'Les noms de lignes de correspondance sont écrits verticalement (un caractère par ligne) pour ne plus gêner les noms de station.'
    'config.tjmetro.cloth_config_hint' = 'Appuyez sur ÉCHAP pour revenir.'
    'chat.tjmetro.ad_link' = 'Lien publicitaire : %s'
    'gui.tjmetro.filter_platforms' = 'Filtrer les quais'
    'gui.tjmetro.play_advertisements' = 'Lire les publicités'
    'gui.tjmetro.use_real_time' = 'Utiliser l''heure réelle'
    'gui.tjmetro.select_advertisements' = 'Choisir une publicité'
    'gamerule.preventBlockFalling' = 'Empêcher la chute des blocs'
    'gamerule.preventBlockFalling.description' = 'Empêche la chute de blocs comme le sable et les enclumes. Règle ajoutée par le mod Métro de Tianjin.'
    'gamerule.playerDetectRange' = 'Portée du détecteur de joueurs'
    'gamerule.playerDetectRange.description' = 'Rayon de détection du détecteur de joueurs. Règle ajoutée par le mod Métro de Tianjin.'
    'gamerule.smokeAlarmRange' = 'Portée du détecteur de fumée'
    'gamerule.smokeAlarmRange.description' = 'Rayon de détection du détecteur de fumée. Règle ajoutée par le mod Métro de Tianjin.'
    'gui.tjmetro.contact_station_for_help' = 'Veuillez vous adresser au personnel de la station.'
    'gui.tjmetro.contact_station_for_help_cjk' = 'Veuillez vous adresser au personnel de la station.'
    'gui.tjmetro.options' = 'Options du Métro de Tianjin'
    'gui.tjmetro.default_color' = 'Couleur par défaut'
    'gui.tjmetro.custom_content' = 'Contenu affiché'
    'gui.tjmetro.terminus' = 'Terminus'
    'gui.tjmetro.terminus_bmt' = 'TERMINUS'
    'gui.tjmetro.next_station' = 'Prochaine station'
    'gui.tjmetro.next_station_format' = 'Prochaine station : %s'
    'gui.tjmetro.next_station_bmt' = 'Proch. station %s'
    'gui.tjmetro.current_station' = 'Station actuelle'
    'gui.tjmetro.this_train' = 'Ce train'
    'gui.tjmetro.bound_for' = 'Direction %s'
    'gui.tjmetro.bound_for_pids' = 'Direction : %s'
    'gui.tjmetro.train_to' = 'À destination de %s'
    'gui.tjmetro.cross_line_train_to' = 'Train inter-lignes vers %s'
    'gui.tjmetro.arriving' = 'Arrivée imminente'
    'gui.tjmetro.arrived' = 'Arrivé'
    'gui.tjmetro.minute' = 'minutes'
    'gui.tjmetro.second' = 'secondes'
    'gui.tjmetro.next_train' = 'Prochain train'
    'gui.tjmetro.station_pinyin' = '%s ZHAN'
    'gui.tjmetro.route_map_bmt_routes' = '%s - %s'
    'gui.tjmetro.filtered_ads' = 'Publicités filtrées (%s)'
    'gui.tjmetro.duplicated_route' = '%s (dupliquée)'
    'gui.tjmetro.outbound_transfer_exit' = 'Correspondance hors zone - valable 10 min'
    'gui.tjmetro.outbound_no_journey' = 'Aucun trajet trouvé - correspondance hors zone impossible'
    'button.tjmetro.click_here' = 'Cliquez ici !'
    'button.tjmetro.open_tutorial' = 'Créer une publicité'
    'button.tjmetro.duplicate_siding_settings' = 'Copier les réglages vers les autres garages'
    'button.tjmetro.duplicate_route' = 'Dupliquer'
    'button.tjmetro.tianjin_metro_options' = 'Options du Métro de Tianjin'
    'config.category.tjmetro' = 'Métro de Tianjin'
    'config.tjmetro.use_tianjin_metro_font' = 'Activer la police du Métro de Tianjin'
    'config.tjmetro.cloth_config_not_found' = '§lCloth Config API§r n''est pas installé.'
    'config.tjmetro.cloth_config_required' = 'Vous devez installer §lCloth Config API§r pour configurer le mod Métro de Tianjin.'
    'config.tjmetro.download_cloth_config' = 'Télécharger Cloth Config'
    'config.tjmetro.rotated_station_name' = 'Faire pivoter les noms de station sur les portes palières (Tianjin)'
    'config.tjmetro.debugging' = 'Options de débogage'
    'config.tjmetro.disable_dynamic_textures' = 'Désactiver les textures dynamiques'
    'config.tjmetro.disable_train_rendering' = 'Désactiver le rendu des trains'
    'config.tjmetro.shrink_dense_station_name' = 'Réduire les noms de station sur les lignes denses'
    'config.tjmetro.vertical_interchange_name' = 'Noms de lignes de correspondance verticaux'
    'config.tjmetro.dynamic_texture_max_size' = 'Taille maximale des textures dynamiques'
    'config.tjmetro.disable_filters' = 'Désactiver les filtres'
    'tooltip.tjmetro.dynamic_texture_max_size' = 'Les textures dynamiques dépassant cette limite seront tronquées. Ajustez ce réglage avec prudence.'
    'tooltip.tjmetro.disable_filters' = 'Vous ne pourrez pas ouvrir l''écran de configuration depuis l''onglet.'
    'tooltip.tjmetro.shift' = 'Maintenez MAJ pour plus d''informations'
    'tooltip.tjmetro.station_name' = 'Le texte dépend de la station'
    'tooltip.tjmetro.custom_content' = 'Prend en charge un contenu affiché personnalisé'
    'tooltip.tjmetro.custom_color' = 'Prend en charge une couleur affichée personnalisée'
    'tooltip.tjmetro.station_name_projector' = 'Petit projecteur de nom de station ; utilisez le pinceau pour changer l''échelle.'
    'tooltip.tjmetro.metal_detection_door' = 'Appareil qui vide certains objets de l''inventaire du joueur. Il supprime directement les objets au lieu de les retirer.'
    'tooltip.tjmetro.use_tianjin_metro_font' = 'Affiche le texte avec la police du mod sur certains blocs.'
    'tooltip.tjmetro.player_detector' = 'Détecte les joueurs à portée et émet un signal de redstone. La portée se règle dans les règles du jeu.'
    'tooltip.tjmetro.roadblock_sign' = 'Utilisez la clé ou le pinceau pour changer le contenu affiché.'
    'tooltip.tjmetro.psd_apg_tianjin' = 'Utilisez la clé pour changer le contenu affiché en haut et le pinceau pour changer le sens des flèches.'
    'tooltip.tjmetro.railway_sign_bmt' = 'Utilisez le mât de panneau ferroviaire de MTR pour prolonger le mât.'
    'tooltip.tjmetro.station_name_entrance_tianjin' = 'Utilisez la clé pour choisir une sortie à afficher et le pinceau pour changer le style.'
    'tooltip.tjmetro.station_navigator' = 'Placez ce bloc au-dessus du « Mât de panneau de guidage ».'
    'tooltip.tjmetro.station_navigator_pole' = 'Placez ce bloc sous « Panneau de guidage ».'
    'tooltip.tjmetro.pids_tianjin' = 'Un PIDS dont la partie publicitaire est personnalisable. Utilisez le mât de panneau (Tianjin) pour le prolonger.'
    'tooltip.tjmetro.railway_sign_wall' = 'Peut être posé au sol. Utilisez la clé pour changer la police.'
    'tooltip.tjmetro.outbound_transfer' = 'Correspondance en dehors de la zone payante, sans terminer le trajet en cours.'
    'tooltip.tjmetro.experimental' = 'Cette fonctionnalité est expérimentale.'
    'tooltip.tjmetro.station_name_plate' = 'Utilisez la clé pour choisir le quai affiché et le pinceau pour changer le sens des flèches.'
    'tooltip.tjmetro.smoke_alarm' = 'Détecte un incendie à portée et émet un signal de redstone. La portée se règle dans les règles du jeu.'
    'tooltip.tjmetro.railway_sign_single' = 'Utilisez un mât d''horloge pour prolonger son mât.'
    'footer.tjmetro.sources' = 'Envie de voir le code source ? §1Cliquez ici !§/'
    'footer.tjmetro.contributors' = 'Le Métro de Tianjin est un travail communautaire, §1cliquez ici§/ pour voir les contributeurs !'
    'footer.tjmetro.forum' = 'Vous souhaitez rejoindre notre §1forum§/ ?'
    'footer.tjmetro.weblate' = '§1Participez à la traduction du Métro de Tianjin !§/'
    'footer.tjmetro.discord' = 'Rejoignez notre serveur Discord !'
    'footer.tjmetro.prevent_block_falling' = 'Saviez-vous que vous pouvez empêcher le sable de tomber ?'
    'footer.tjmetro.bde' = 'Essayez aussi Bomb Disposal Expert !'
    'footer.tjmetro.sponsor' = 'Soutenez le développement du Métro de Tianjin !'
    'footer.tjmetro.tutorial' = 'Regardez les vidéos de présentation/tutoriel !'
    'advertisement.tjmetro.name' = 'Métro de Tianjin'
    'advertisement.tjmetro.description' = 'Publicités par défaut du mod Métro de Tianjin.'
    'advertisement.tjmetro.banner' = 'Le mod Métro de Tianjin est une extension de Minecraft Transit Railway reprenant de nombreux éléments du métro de Tianjin.'
    'advertisement.tjmetro.weblate' = 'Participez à la traduction du Métro de Tianjin !'
    'advertisement.tjmetro.bde' = 'Bomb Disposal Expert est un mod amusant pour désamorcer des bombes et des Creepers !'
    'sign.tjmetro.tianjin_metro' = 'Métro de Tianjin'
    'sign.tjmetro.train' = 'Train'
    'sign.tjmetro.station' = 'Station'
    'sign.tjmetro.emergency_exit' = 'Sortie de secours'
    'sign.tjmetro.no_throughfare' = 'Passage interdit'
    'sign.tjmetro.accessible_elevator' = 'Ascenseur accessible'
    'sign.tjmetro.automatic_ticket' = 'Distributeur de billets'
    'sign.tjmetro.railway_station' = 'Gare ferroviaire'
    'sign.tjmetro.accessible_toilet' = 'Toilettes accessibles'
    'sign.tjmetro.fare_adjustment' = 'Régularisation du tarif'
    'sign.tjmetro.inquiry' = 'Renseignements'
    'sign.tjmetro.bound_for' = 'Direction %s'
    'sign.tjmetro.accessible_passage' = 'Passage accessible'
    'sign.tjmetro.toilet' = 'Toilettes'
    'sign.tjmetro.no_entry_bmt' = 'Entrée interdite'
    'sign.tjmetro.to_subway_bmt' = 'Vers le métro'
    'sign.tjmetro.exit_bmt' = 'Sortie'
    'sign.tjmetro.tickets_bmt' = 'Billets'
    'sign.tjmetro.security_check' = 'Contrôle de sécurité'
    'sign.tjmetro.customer_service_center' = 'Service clientèle|Customer Service Center'
    'sign.tjmetro.baby_care' = 'Espace bébé|Baby Care'
    'sign.tjmetro.police_office' = 'Bureau de police|Police Office'
    'sign.tjmetro.outbound_transfer' = 'Correspondance hors zone|Outbound Transfer'
    'sign.tjmetro.self_service_ticketing' = 'Achat de billets en libre-service|Self-Service Ticketing'
    'sign.tjmetro.to_subway' = 'Vers le métro|To Subway'
    'sign.tjmetro.train_to' = 'À destination de|To'
    'sign.tjmetro.cross_line_train_to' = 'Train inter-lignes vers|Cross Line Train To'
    'sign.tjmetro.to_train_bmt' = 'Vers le train|To Train'
    'item.tjmetro.wrench' = 'Clé'
    'block.tjmetro.service_corridor_sign' = 'Panneau « Couloir de service »'
    'block.tjmetro.station_sign' = 'Panneau de station'
    'block.tjmetro.no_entering_sign' = 'Panneau « Entrée interdite »'
    'gui.tjmetro.outbound_transfer_invalid' = 'Entrée à %s - correspondance hors zone impossible - dernier tarif : %s$'
    'gui.tjmetro.outbound_transfer_expired' = 'Entrée à %s - correspondance hors zone expirée - dernier tarif : %s$'
    'gui,tjmetro.outbound_transfer_success' = 'Correspondance hors zone - poursuite du trajet précédent'
}

$twCurated = @{
    'itemGroup.tjmetro.tab' = '天津地鐵'
    'itemGroup.tjmetro.tjmetro_tab' = '天津地鐵'
    'modmenu.nameTranslation.tjmetro' = '天津地鐵'
    'modmenu.descriptionTranslation.tjmetro' = '一個 Minecraft Transit Railway 的附屬模組'
    'sign.tjmetro.tianjin_metro_cjk' = '天津地鐵'
    'sign.tjmetro.train_cjk' = '列車'
    'sign.tjmetro.station_cjk' = '站名'
    'sign.tjmetro.emergency_exit_cjk' = '緊急出口'
    'sign.tjmetro.no_throughfare_cjk' = '禁止通行'
    'sign.tjmetro.accessible_elevator_cjk' = '無障礙電梯'
    'sign.tjmetro.automatic_ticket_cjk' = '自動售票'
    'sign.tjmetro.railway_station_cjk' = '火車站'
    'sign.tjmetro.accessible_toilet_cjk' = '無障礙洗手間'
    'sign.tjmetro.fare_adjustment_cjk' = '補票'
    'sign.tjmetro.inquiry_cjk' = '詢問'
    'sign.tjmetro.bound_for_cjk' = '%s方向'
    'sign.tjmetro.accessible_passage_cjk' = '無障礙通道'
    'sign.tjmetro.toilet_cjk' = '洗手間'
    'sign.tjmetro.no_entry_bmt_cjk' = '乘客止步'
    'sign.tjmetro.to_subway_bmt_cjk' = '乘車'
    'sign.tjmetro.exit_bmt_cjk' = '出口'
    'sign.tjmetro.tickets_bmt_cjk' = '售票處'
    'gui.tjmetro.contact_station_for_help_cjk' = '如需協助請聯繫車站'
    'gui.tjmetro.next_station_cjk' = '下一站'
    'gui.tjmetro.terminus_cjk' = '終點站'
    'gui.tjmetro.current_station_cjk' = '當前站'
    'gui.tjmetro.this_train_cjk' = '本班列車'
    'gui.tjmetro.arriving_cjk' = '即將進站'
    'gui.tjmetro.arrived_cjk' = '已到站'
    'gui.tjmetro.minute_cjk' = '分鐘'
    'gui.tjmetro.second_cjk' = '秒'
    'gui.tjmetro.next_train_cjk' = '下一班列車'
    'gui.tjmetro.train_to_cjk' = '開往%s'
    'gui.tjmetro.cross_line_train_to_cjk' = '跨線車往%s'
    'gui.tjmetro.bound_for_cjk' = '%s方向'
    'gui.tjmetro.bound_for_pids_cjk' = '開往：%s'
    'gui.tjmetro.next_station_bmt_cjk' = '下一站 %s'
    'gui.tjmetro.bound_for_bmt_cjk' = '開往 %s'
    'gui.tjmetro.next_station_format_cjk' = '下一站 %s'
    'gui.tjmetro.service_corridor_sign_cjk' = '站內服務連廊'
    'gui.tjmetro.accept_security_check_cjk' = '請接受安全檢查'
}

# ---------------------------------------------------------------- 组装
$jaUp = Load-Upstream 'ja_jp.json'
$frUp = Load-Upstream 'fr_fr.json'
$twUp = Load-Upstream 'zh_tw.json'

$result = @{
    'ja_jp' = [ordered]@{}
    'fr_fr' = [ordered]@{}
    'zh_tw' = [ordered]@{}
}

foreach ($key in $keys) {
    $enVal = $enMap[$key]
    $zhVal = $zhMap[$key]

    # ja
    if ($jaCurated.ContainsKey($key)) { $result['ja_jp'][$key] = $jaCurated[$key] }
    elseif ($jaUp.ContainsKey($key) -and -not [string]::IsNullOrWhiteSpace($jaUp[$key])) { $result['ja_jp'][$key] = $jaUp[$key] }
    else { $result['ja_jp'][$key] = Apply-Rules $enVal $jaRules }

    # fr
    if ($frCurated.ContainsKey($key)) { $result['fr_fr'][$key] = $frCurated[$key] }
    elseif ($frUp.ContainsKey($key) -and -not [string]::IsNullOrWhiteSpace($frUp[$key])) { $result['fr_fr'][$key] = $frUp[$key] }
    else { $result['fr_fr'][$key] = Apply-Rules $enVal $frRules }

    # zh_tw
    if ($twCurated.ContainsKey($key)) { $result['zh_tw'][$key] = $twCurated[$key] }
    elseif ($twUp.ContainsKey($key) -and -not [string]::IsNullOrWhiteSpace($twUp[$key])) { $result['zh_tw'][$key] = $twUp[$key] }
    elseif (-not [string]::IsNullOrWhiteSpace($zhVal)) { $result['zh_tw'][$key] = Convert-ZhTw $zhVal }
    else { $result['zh_tw'][$key] = $enVal }
}

function Write-Lang([string]$fileName, $ordered) {
    $lines = New-Object System.Collections.Generic.List[string]
    $lines.Add('{')
    $i = 0
    $total = $ordered.Count
    foreach ($k in $ordered.Keys) {
        $i++
        $v = $ordered[$k]
        $v = [regex]::Replace($v, ' {2,}', ' ')
        $vEsc = $v.Replace('\', '\\').Replace('"', '\"').Replace("`r", '').Replace("`n", '\n')
        $kEsc = $k.Replace('\', '\\').Replace('"', '\"')
        $comma = if ($i -lt $total) { ',' } else { '' }
        $lines.Add(('    "{0}": "{1}"{2}' -f $kEsc, $vEsc, $comma))
    }
    $lines.Add('}')
    $content = ($lines -join "`r`n") + "`r`n"
    $utf8 = New-Object System.Text.UTF8Encoding($false)
    foreach ($dir in $langDirs) {
        $path = Join-Path $dir $fileName
        [IO.File]::WriteAllText($path, $content, $utf8)
    }
    Write-Host ("wrote {0} ({1} keys) into {2} module(s)" -f $fileName, $total, $langDirs.Count)
}

Write-Lang 'ja_jp.json' $result['ja_jp']
Write-Lang 'fr_fr.json' $result['fr_fr']
Write-Lang 'zh_tw.json' $result['zh_tw']
