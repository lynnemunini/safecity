package com.grayseal.safecity.screens.statistics

import android.graphics.Typeface
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grayseal.safecity.R
import com.grayseal.safecity.model.SafeCityItem
import com.grayseal.safecity.screens.main.StoreHotspotAreas
import com.grayseal.safecity.ui.theme.Green
import com.grayseal.safecity.ui.theme.LightGreen
import com.grayseal.safecity.ui.theme.poppinsFamily
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.edges.rememberFadingEdges
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.DefaultColors
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun ChartsScreen(navController: NavController, id: String) {
    val context = LocalContext.current
    val storeHotspotAreas = StoreHotspotAreas(context = context)
    var nearbyHotspots by remember { mutableStateOf(emptyList<SafeCityItem>()) }
    var loading by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        val hotspots = storeHotspotAreas.retrieveNearbyHotspots()
        if (hotspots != null) {
            nearbyHotspots = hotspots
            loading = false
        }
    }

    val data = nearbyHotspots.find { it.Id == id }
    val chartColors = listOf(Green, Yellow)
    val yFraction = 1.2f
    val axisValueOverrider =
        AxisValuesOverrider.adaptiveYValues(
            yFraction = yFraction,
            round = true
        )
    val axisTitleHorizontalPaddingValue = 8.dp
    val axisTitleVerticalPaddingValue = 2.dp
    val axisTitlePadding =
        dimensionsOf(axisTitleHorizontalPaddingValue, axisTitleVerticalPaddingValue)
    val axisTitleMarginValue = 4.dp
    val startAxisTitleMargins = dimensionsOf(end = axisTitleMarginValue)
    val bottomAxisTitleMargins = dimensionsOf(top = axisTitleMarginValue)
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val monthsOfYear =
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val categoriesOfCrime = listOf("Assault", "Burglary", "Fraud", "Theft", "Vandalism")
    val daysBottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> daysOfWeek[x.toInt() % daysOfWeek.size] }
    val monthsBottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> monthsOfYear[x.toInt() % monthsOfYear.size] }
    val categoriesBottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> categoriesOfCrime[x.toInt() % categoriesOfCrime.size] }

    if (data != null) {
        val days = entryModelOf(
            data.Days.Monday,
            data.Days.Tuesday,
            data.Days.Wednesday,
            data.Days.Thursday,
            data.Days.Friday,
            data.Days.Saturday,
            data.Days.Sunday
        )

        val months = entryModelOf(
            data.Months.January,
            data.Months.February,
            data.Months.March,
            data.Months.April,
            data.Months.May,
            data.Months.June,
            data.Months.July,
            data.Months.August,
            data.Months.September,
            data.Months.October,
            data.Months.November,
            data.Months.December
        )

        val categories = entryModelOf(
            data.Categories.assault,
            data.Categories.burglary,
            data.Categories.fraud,
            data.Categories.theft,
            data.Categories.vandalism
        )
        Charts(
            navController = navController,
            data = data,
            chartColors = chartColors,
            axisValueOverrider = axisValueOverrider,
            axisTitlePadding = axisTitlePadding,
            startAxisTitleMargins = startAxisTitleMargins,
            bottomAxisTitleMargins = bottomAxisTitleMargins,
            daysBottomAxisValueFormatter = daysBottomAxisValueFormatter,
            monthsBottomAxisValueFormatter = monthsBottomAxisValueFormatter,
            categoriesBottomAxisValueFormatter = categoriesBottomAxisValueFormatter,
            days = days,
            months = months,
            categories = categories
        )
    }
}

@Composable
fun Charts(
    navController: NavController,
    data: SafeCityItem,
    chartColors: List<Color>,
    axisValueOverrider: AxisValuesOverrider<ChartEntryModel>,
    axisTitlePadding: MutableDimensions,
    startAxisTitleMargins: MutableDimensions,
    bottomAxisTitleMargins: MutableDimensions,
    daysBottomAxisValueFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    monthsBottomAxisValueFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    categoriesBottomAxisValueFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    days: ChartEntryModel,
    months: ChartEntryModel,
    categories: ChartEntryModel
) {
    Surface(modifier = Modifier.fillMaxSize(), color = LightGreen) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = {
                            navController.popBackStack()
                        })
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Charts & Graphs", fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                    Text(
                        text = data.LocationName,
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // DAYS CHART
                    rememberChartStyle(
                        columnChartColors = chartColors,
                        lineChartColors = listOf(White)
                    )
                    ProvideChartStyle(rememberChartStyle(chartColors, listOf(White))) {
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            color = Green,
                            border = BorderStroke(width = 10.dp, color = White),
                            elevation = 4.dp
                        ) {
                            Column {
                                Chart(
                                    chart = lineChart(
                                        axisValuesOverrider = axisValueOverrider
                                    ),
                                    model = days,
                                    modifier = Modifier.padding(
                                        horizontal = 20.dp,
                                        vertical = 10.dp
                                    ),
                                    startAxis = startAxis(
                                        guideline = null,
                                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                                        titleComponent = textComponent(
                                            color = White,
                                            background = shapeComponent(Shapes.pillShape, Green),
                                            padding = axisTitlePadding,
                                            margins = startAxisTitleMargins,
                                            typeface = Typeface.MONOSPACE,
                                        ),
                                        title = "Reports",
                                    ),
                                    bottomAxis = bottomAxis(
                                        titleComponent = textComponent(
                                            background = shapeComponent(Shapes.pillShape, Green),
                                            color = White,
                                            padding = axisTitlePadding,
                                            margins = bottomAxisTitleMargins,
                                            typeface = Typeface.MONOSPACE,
                                        ),
                                        title = "Days",
                                        valueFormatter = daysBottomAxisValueFormatter,
                                    ),
                                    fadingEdges = rememberFadingEdges(),
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 30.dp, bottom = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = com.grayseal.safecity.R.drawable.circle),
                                        contentDescription = "Bullet",
                                        tint = White,
                                        modifier = Modifier.size(5.dp)
                                    )
                                    Text(
                                        "Notorious Day -> " + data.NotoriousDay,
                                        fontWeight = FontWeight.Medium,
                                        color = White,
                                        fontFamily = poppinsFamily,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // MONTHS CHART
                    ProvideChartStyle(rememberChartStyle(listOf(Color(0xFFf3a52d)), chartColors)) {
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            color = White,
                            elevation = 4.dp
                        ) {
                            Column {
                                Chart(
                                    chart = columnChart(
                                        mergeMode = ColumnChart.MergeMode.Grouped,
                                        targetVerticalAxisPosition = AxisPosition.Vertical.Start,
                                        axisValuesOverrider = axisValueOverrider
                                    ),
                                    model = months,
                                    modifier = Modifier.padding(
                                        horizontal = 20.dp,
                                        vertical = 10.dp
                                    ),
                                    startAxis = startAxis(
                                        guideline = null,
                                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                                        titleComponent = textComponent(
                                            color = White,
                                            background = shapeComponent(
                                                Shapes.pillShape,
                                                Color(0xFFf3a52d)
                                            ),
                                            padding = axisTitlePadding,
                                            margins = startAxisTitleMargins,
                                            typeface = Typeface.MONOSPACE,
                                        ),
                                        title = "Reports",
                                    ),
                                    bottomAxis = bottomAxis(
                                        titleComponent = textComponent(
                                            background = shapeComponent(
                                                Shapes.pillShape,
                                                Color(0xFFf3a52d)
                                            ),
                                            color = White,
                                            padding = axisTitlePadding,
                                            margins = bottomAxisTitleMargins,
                                            typeface = Typeface.MONOSPACE,
                                        ),
                                        title = "Months",
                                        valueFormatter = monthsBottomAxisValueFormatter,
                                    ),
                                    fadingEdges = rememberFadingEdges(),
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 30.dp, bottom = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = com.grayseal.safecity.R.drawable.circle),
                                        contentDescription = "Bullet",
                                        tint = Color(0xFFf3a52d),
                                        modifier = Modifier.size(5.dp)
                                    )
                                    Text(
                                        "Notorious Month -> " + data.NotoriousMonth,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFf3a52d),
                                        fontFamily = poppinsFamily,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // CATEGORIES CHART
                    ProvideChartStyle(rememberChartStyle(chartColors, chartColors)) {
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            color = White,
                            border = BorderStroke(width = 10.dp, color = Green),
                            elevation = 4.dp,
                            modifier = Modifier.padding(bottom = 20.dp)
                        ) {
                            Column {
                                Chart(
                                    chart = lineChart(
                                        axisValuesOverrider = axisValueOverrider
                                    ),
                                    model = categories,
                                    modifier = Modifier.padding(
                                        vertical = 10.dp,
                                        horizontal = 20.dp
                                    ),
                                    startAxis = startAxis(
                                        guideline = null,
                                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                                        titleComponent = textComponent(
                                            color = White,
                                            background = shapeComponent(Shapes.pillShape, Green),
                                            padding = axisTitlePadding,
                                            margins = startAxisTitleMargins,
                                            typeface = Typeface.MONOSPACE,
                                        ),
                                        title = "Reports",
                                    ),
                                    bottomAxis = bottomAxis(
                                        titleComponent = textComponent(
                                            background = shapeComponent(Shapes.pillShape, Green),
                                            color = White,
                                            padding = axisTitlePadding,
                                            margins = bottomAxisTitleMargins,
                                            typeface = Typeface.MONOSPACE,
                                        ),
                                        title = "Categories",
                                        valueFormatter = categoriesBottomAxisValueFormatter,
                                    ),
                                    fadingEdges = rememberFadingEdges(),
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 30.dp, bottom = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.circle),
                                        contentDescription = "Bullet",
                                        tint = Green,
                                        modifier = Modifier.size(5.dp)
                                    )
                                    Text(
                                        "Frequent Crime -> " + data.FrequentCrime,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = poppinsFamily,
                                        fontSize = 13.sp,
                                        color = Green
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun rememberChartStyle(
    columnChartColors: List<Color>,
    lineChartColors: List<Color>
): ChartStyle {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    return remember(columnChartColors, lineChartColors, isSystemInDarkTheme) {
        val defaultColors = if (isSystemInDarkTheme) DefaultColors.Dark else DefaultColors.Light
        ChartStyle(
            ChartStyle.Axis(
                axisLabelColor = Color(defaultColors.axisLabelColor),
                axisGuidelineColor = Color(defaultColors.axisGuidelineColor),
                axisLineColor = Color(defaultColors.axisLineColor),
            ),
            ChartStyle.ColumnChart(
                columnChartColors.map { columnChartColor ->
                    LineComponent(
                        columnChartColor.toArgb(),
                        DefaultDimens.COLUMN_WIDTH,
                        Shapes.roundedCornerShape(DefaultDimens.COLUMN_ROUNDNESS_PERCENT),
                    )
                },
            ),
            ChartStyle.LineChart(
                lineChartColors.map { lineChartColor ->
                    LineChart.LineSpec(
                        lineColor = lineChartColor.toArgb(),
                        lineBackgroundShader = DynamicShaders.fromBrush(
                            Brush.verticalGradient(
                                listOf(
                                    lineChartColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                    lineChartColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END),
                                ),
                            ),
                        ),
                    )
                },
            ),
            ChartStyle.Marker(),
            Color(defaultColors.elevationOverlayColor),
        )
    }
}
