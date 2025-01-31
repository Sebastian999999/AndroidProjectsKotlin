package com.hammadirfan.smdproject

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*

class DashboardFragment : Fragment() {

    private lateinit var chartSales: LineChart
    private lateinit var chartProfit: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        chartSales = view.findViewById(R.id.chartSales)
        chartProfit = view.findViewById(R.id.chartProfit)

        setupSalesChart()
        setupProfitChart()

        return view
    }

    private fun setupSalesChart() {
        val entries = listOf(
            Entry(0f, 50f),
            Entry(1f, 60f),
            Entry(2f, 40f),
            Entry(3f, 70f),
            Entry(4f, 30f)
        )
        val dataSet = LineDataSet(entries, "Sales").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }
        chartSales.data = LineData(dataSet)
        chartSales.invalidate() // refresh chart
    }

    private fun setupProfitChart() {
        val entries = listOf(
            BarEntry(0f, 20f),
            BarEntry(1f, 30f),
            BarEntry(2f, 40f),
            BarEntry(3f, 10f),
            BarEntry(4f, 50f)
        )
        val dataSet = BarDataSet(entries, "Profit").apply {
            color = Color.CYAN
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }
        chartProfit.data = BarData(dataSet)
        chartProfit.invalidate() // refresh chart
    }
}