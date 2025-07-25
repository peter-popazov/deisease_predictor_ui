package com.peter.rgr

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.peter.rgr.viewmodel.ResultsViewModel

class ResultsActivity : AppCompatActivity() {

    private lateinit var viewModel: ResultsViewModel
    private lateinit var pieChart: PieChart
    private lateinit var textViewRiskPercentage: TextView
    private lateinit var textViewRiskLevel: TextView
    private lateinit var textViewRecommendation: TextView
    private lateinit var cardViewRecommendation: CardView
    private lateinit var buttonReturnHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        
        viewModel = ViewModelProvider(this)[ResultsViewModel::class.java]
        
        initializeViews()
        observeViewModel()

        // Calculate prediction based on collected data
        viewModel.calculatePrediction()

        // Set up return button
        buttonReturnHome.setOnClickListener {
            val intent = Intent(this, PatientDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
    
    private fun initializeViews() {
        pieChart = findViewById(R.id.pieChart)
        textViewRiskPercentage = findViewById(R.id.textViewRiskPercentage)
        textViewRiskLevel = findViewById(R.id.textViewRiskLevel)
        textViewRecommendation = findViewById(R.id.textViewRecommendation)
        cardViewRecommendation = findViewById(R.id.cardViewRecommendation)
        buttonReturnHome = findViewById(R.id.buttonReturnHome)
    }
    
    private fun observeViewModel() {
        viewModel.predictionResult.observe(this) { result ->
            updateChartAndDisplay(result)
        }

        viewModel.recommendations.observe(this) { recommendations ->
            // Display recommendations as bullet points
            val bulletPoints = recommendations.joinToString(separator = "\n") { "• $it" }
            textViewRecommendation.text = bulletPoints
        }
    }
    
    private fun updateChartAndDisplay(riskPercentage: Float) {
        // Update text views
        textViewRiskPercentage.text = "${riskPercentage.toInt()}%"
        
        // Set risk level text and color
        when {
            riskPercentage < 30 -> {
                textViewRiskLevel.text = "Low Risk"
                textViewRiskLevel.setTextColor(Color.parseColor("#4CAF50")) // Green
//                textViewRecommendation.text = "Your risk for Alzheimer's seems low based on the data provided. Continue maintaining a healthy lifestyle. Consider regular cognitive assessments as you age."
            }
            riskPercentage < 60 -> {
                textViewRiskLevel.text = "Moderate Risk"
                textViewRiskLevel.setTextColor(Color.parseColor("#FFA000")) // Amber
//                textViewRecommendation.text = "Your results indicate a moderate risk. Consider consulting with a neurologist for further assessment. Lifestyle modifications may help reduce risk factors."
            }
            else -> {
                textViewRiskLevel.text = "High Risk"
                textViewRiskLevel.setTextColor(Color.parseColor("#F44336")) // Red
//                textViewRecommendation.text = "Your results indicate a high risk. We strongly recommend consulting with a healthcare professional specializing in neurological disorders for a comprehensive evaluation."
            }
        }
        
        // Setup pie chart
        setupPieChart(riskPercentage)
    }
    
    private fun setupPieChart(riskPercentage: Float) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(riskPercentage, "Risk"))
        entries.add(PieEntry(100f - riskPercentage, ""))
        
        val dataSet = PieDataSet(entries, "")
        
        // Set colors based on risk level
        val colors = ArrayList<Int>()
        val riskColor = when {
            riskPercentage < 30 -> Color.parseColor("#4CAF50") // Green
            riskPercentage < 60 -> Color.parseColor("#FFA000") // Amber
            else -> Color.parseColor("#F44336") // Red
        }
        colors.add(riskColor)
        colors.add(Color.parseColor("#EEEEEE")) // Light grey for remaining portion
        
        dataSet.colors = colors
        dataSet.setDrawValues(false)
        
        val data = PieData(dataSet)
        pieChart.data = data
        
        // Configure chart appearance
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.holeRadius = 80f
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setDrawCenterText(false)
        pieChart.isRotationEnabled = false
        pieChart.isHighlightPerTapEnabled = false
        
        // Animate chart
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}
