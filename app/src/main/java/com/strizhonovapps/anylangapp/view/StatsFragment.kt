package com.strizhonovapps.anylangapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.di.DaggerStatsFragmentComponent
import com.strizhonovapps.anylangapp.di.WordServiceModule
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.viewsupport.VerticalTextView
import java.util.*
import javax.inject.Inject

class StatsFragment : Fragment() {

    private var barChart: BarChart? = null
    private var ifEmptyTextView: TextView? = null
    private var xAxisTv: TextView? = null
    private var yAxisTv: VerticalTextView? = null
    private lateinit var mView: View

    @Inject
    lateinit var wordService: WordService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        DaggerStatsFragmentComponent.builder()
                .wordServiceModule(WordServiceModule(requireContext()))
                .build()
                .inject(this)

        mView = inflater.inflate(R.layout.fragment_stats, container, false)!!
        drawChart(mView)
        return mView
    }

    override fun onResume() {
        super.onResume()
        drawChart(mView)
    }

    private fun drawChart(view: View) {
        barChart = view.findViewById(R.id.barChart)
        ifEmptyTextView = view.findViewById(R.id.if_empty_text_view)
        xAxisTv = view.findViewById(R.id.x_axis_name_text_view)
        yAxisTv = view.findViewById(R.id.y_axis_name_text_view)
        val levelMap = getLevelsCountsMap()
        if (levelMap.isEmpty()) {
            setEmptyVisibility()
            return
        }
        setDefaultVisibility()
        initializeBarChart(barChart!!, levelMap)
    }

    private fun setDefaultVisibility() {
        barChart!!.visibility = View.VISIBLE
        xAxisTv!!.visibility = View.VISIBLE
        yAxisTv!!.visibility = View.VISIBLE
        ifEmptyTextView!!.visibility = View.GONE
    }

    private fun setEmptyVisibility() {
        barChart!!.visibility = View.GONE
        xAxisTv!!.visibility = View.GONE
        yAxisTv!!.visibility = View.GONE
        ifEmptyTextView!!.visibility = View.VISIBLE
    }

    private fun initializeBarChart(barChart: BarChart, levelMap: Map<Int?, Int>) {
        configureBarChart(barChart)
        configureXAxis(barChart)
        configureYAxis(barChart)
        configureBarData(barChart, levelMap)

        barChart.invalidate()
    }

    private fun configureBarChart(barChart: BarChart?) {
        barChart!!.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description = Description()
        barChart.description.text = ""
        barChart.setMaxVisibleValueCount(50)
        barChart.setPinchZoom(true)
        barChart.setDrawGridBackground(true)
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false
    }

    private fun configureBarData(barChart: BarChart, levelMap: Map<Int?, Int>) {
        val yVals = valuesToBars(levelMap)
        if (barChart.data != null && barChart.data.dataSetCount > 0) {
            setExistingDataToChart(barChart, yVals)
        } else {
            setEmptyDataToChart(yVals, barChart)
        }
    }

    private fun configureYAxis(barChart: BarChart) {
        barChart.axisLeft.granularity = 1f
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisLeft.spaceTop = 30f
    }

    private fun configureXAxis(barChart: BarChart) {
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setCenterAxisLabels(false)
    }

    private fun setExistingDataToChart(barChart: BarChart, yVals: List<BarEntry>) {
        val barDataSet = barChart.data.getDataSetByIndex(0) as BarDataSet
        barDataSet.values = yVals
        barChart.data.notifyDataChanged()
        barChart.notifyDataSetChanged()
        barChart.barData.barWidth = 0.7f
    }

    private fun setEmptyDataToChart(yVals: List<BarEntry>, barChart: BarChart) {
        val barDataSet = BarDataSet(yVals, "")
        val context: Context? = this.context
        if (context != null) {
            val color = ContextCompat.getColor(context, R.color.colorAccent)
            barDataSet.color = color
        }
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)
        barChart.data = BarData(dataSets)
    }

    private fun valuesToBars(levelMap: Map<Int?, Int>): List<BarEntry> {
        val yVals: MutableList<BarEntry> = ArrayList()
        var i = 0
        var currentLevel = 0
        while (i < levelMap.size) {
            val currentLevelCount = levelMap[currentLevel] ?: 0
            if (currentLevelCount == 0) {
                currentLevel++
                continue
            }
            yVals.add(BarEntry(currentLevel.toFloat(), currentLevelCount.toFloat()))
            i++
            currentLevel++
        }
        return yVals
    }

    /**
     * Get map of words' data, where keys are levels of the word,
     * and values - word amount of words at this level
     *
     * @return map of words' data, where keys are levels of the word,
     * and values - word amount at this level
     */
    private fun getLevelsCountsMap(): Map<Int?, Int> {
        val levelMap = TreeMap<Int?, Int>()
        val words = wordService.findAll()
        if (words.isEmpty()) {
            return levelMap
        }

        for ((_, _, _, lvl) in words) {
            if (levelMap.containsKey(lvl)) {
                val primitiveMapValue = levelMap[lvl] ?: 0
                levelMap[lvl] = primitiveMapValue + 1
            } else {
                levelMap[lvl] = 1
            }
        }
        return levelMap
    }

}