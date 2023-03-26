package com.grayseal.safecity.screens.statistics

import android.graphics.Typeface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.grayseal.safecity.model.SafeCityItem
import com.grayseal.safecity.screens.main.StoreHotspotAreas
import com.grayseal.safecity.ui.theme.Green
import com.grayseal.safecity.ui.theme.Pink
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
    val monthsOfYear = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val categoriesOfCrime = listOf("Assault", "Burglary", "Fraud", "Theft", "Vandalism")
    val daysBottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> daysOfWeek[x.toInt() % daysOfWeek.size] }
    val monthsBottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> monthsOfYear[x.toInt() % monthsOfYear.size] }
    val categoriesBottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> categoriesOfCrime[x.toInt() % categoriesOfCrime.size] }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // DAYS CHART
            rememberChartStyle(columnChartColors = chartColors, lineChartColors = listOf(Pink))
            ProvideChartStyle(rememberChartStyle(chartColors, listOf(Pink))) {
                Chart(
                    chart = lineChart(
                        axisValuesOverrider = axisValueOverrider
                    ),
                    model = days,
                    modifier = Modifier,
                    startAxis = startAxis(
                        guideline = null,
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                        titleComponent = textComponent(
                            color = Color.White,
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
                            color = Color.White,
                            padding = axisTitlePadding,
                            margins = bottomAxisTitleMargins,
                            typeface = Typeface.MONOSPACE,
                        ),
                        title = "Days",
                        valueFormatter = daysBottomAxisValueFormatter,
                    ),
                    fadingEdges = rememberFadingEdges(),
                )
            }

            ProvideChartStyle(rememberChartStyle(chartColors, listOf(Pink))) {
                Chart(
                    chart = columnChart(
                        mergeMode = ColumnChart.MergeMode.Grouped,
                        targetVerticalAxisPosition = AxisPosition.Vertical.Start,
                        axisValuesOverrider = axisValueOverrider
                    ),
                    model = months,
                    modifier = Modifier,
                    startAxis = startAxis(
                        guideline = null,
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                        titleComponent = textComponent(
                            color = Color.White,
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
                            color = Color.White,
                            padding = axisTitlePadding,
                            margins = bottomAxisTitleMargins,
                            typeface = Typeface.MONOSPACE,
                        ),
                        title = "Months",
                        valueFormatter = monthsBottomAxisValueFormatter,
                    ),
                    fadingEdges = rememberFadingEdges(),
                )
            }

            ProvideChartStyle(rememberChartStyle(chartColors, listOf(Pink))) {
                Chart(
                    chart = lineChart(
                        axisValuesOverrider = axisValueOverrider
                    ),
                    model = categories,
                    modifier = Modifier,
                    startAxis = startAxis(
                        guideline = null,
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                        titleComponent = textComponent(
                            color = Color.White,
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
                            color = Color.White,
                            padding = axisTitlePadding,
                            margins = bottomAxisTitleMargins,
                            typeface = Typeface.MONOSPACE,
                        ),
                        title = "Categories",
                        valueFormatter = categoriesBottomAxisValueFormatter,
                    ),
                    fadingEdges = rememberFadingEdges(),
                )
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
