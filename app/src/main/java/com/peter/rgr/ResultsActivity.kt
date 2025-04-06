package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator

class ResultsActivity : AppCompatActivity() {
    
    private lateinit var textViewRiskLevel: TextView
    private lateinit var progressMemory: LinearProgressIndicator
    private lateinit var progressCognitive: LinearProgressIndicator
    private lateinit var textViewRecommendations: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        
        // Initialize views
        textViewRiskLevel = findViewById(R.id.textViewRiskLevel)
        progressMemory = findViewById(R.id.progressMemory)
        progressCognitive = findViewById(R.id.progressCognitive)
        textViewRecommendations = findViewById(R.id.textViewRecommendations)
        
        // Calculate and display results
        calculateResults()
        
        // Set up button listeners
        findViewById<MaterialButton>(R.id.buttonShare).setOnClickListener {
            shareResults()
        }
        
        findViewById<MaterialButton>(R.id.buttonStartOver).setOnClickListener {
            startOver()
        }
    }
    
    private fun calculateResults() {
        val sharedPrefs = getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
        
        // Calculate Memory Score
        val recentMemoryScore = sharedPrefs.getFloat("recent_memory_score", 0f)
        val appointmentsMemory = sharedPrefs.getInt("appointments_memory", 0)
        val memoryImpact = sharedPrefs.getFloat("memory_impact", 0f)
        
        val memoryScore = calculateMemoryScore(recentMemoryScore, appointmentsMemory, memoryImpact)
        progressMemory.progress = memoryScore
        
        // Calculate Cognitive Score
        val problemSolvingScore = sharedPrefs.getFloat("problem_solving_score", 0f)
        val languageSkills = sharedPrefs.getInt("language_skills", 0)
        val attentionSpan = sharedPrefs.getInt("attention_span", 0)
        val decisionMaking = sharedPrefs.getFloat("decision_making", 0f)
        
        val cognitiveScore = calculateCognitiveScore(problemSolvingScore, languageSkills, attentionSpan, decisionMaking)
        progressCognitive.progress = cognitiveScore
        
        // Determine Risk Level
        val overallRisk = determineRiskLevel(memoryScore, cognitiveScore)
        updateRiskLevel(overallRisk)
        
        // Set Recommendations
        updateRecommendations(overallRisk)
    }
    
    private fun calculateMemoryScore(recentMemory: Float, appointments: Int, impact: Float): Int {
        // Implement scoring logic based on memory assessment
        val score = ((recentMemory + (3 - appointments) * 3.33 + impact) / 3) * 10
        return score.toInt()
    }
    
    private fun calculateCognitiveScore(
        problemSolving: Float,
        language: Int,
        attention: Int,
        decision: Float
    ): Int {
        // Implement scoring logic based on cognitive assessment
        val languageScore = (3 - language) * 3.33
        val attentionScore = (attention.coerceAtMost(60) / 60.0) * 10
        return ((problemSolving + languageScore + attentionScore + decision) / 4 * 10).toInt()
    }
    
    private fun determineRiskLevel(memoryScore: Int, cognitiveScore: Int): RiskLevel {
        val averageScore = (memoryScore + cognitiveScore) / 2
        return when {
            averageScore >= 80 -> RiskLevel.LOW
            averageScore >= 60 -> RiskLevel.MODERATE
            else -> RiskLevel.HIGH
        }
    }
    
    private fun updateRiskLevel(risk: RiskLevel) {
        textViewRiskLevel.text = risk.toString()
        textViewRiskLevel.setTextColor(
            when (risk) {
                RiskLevel.LOW -> getColor(android.R.color.holo_green_dark)
                RiskLevel.MODERATE -> getColor(android.R.color.holo_orange_dark)
                RiskLevel.HIGH -> getColor(android.R.color.holo_red_dark)
            }
        )
    }
    
    private fun updateRecommendations(risk: RiskLevel) {
        val recommendations = when (risk) {
            RiskLevel.LOW -> """
                Your assessment indicates a low risk for Alzheimer's disease. To maintain cognitive health:
                • Stay physically active
                • Maintain social connections
                • Challenge your mind with puzzles and new learning
                • Continue regular health check-ups
            """.trimIndent()
            
            RiskLevel.MODERATE -> """
                Your assessment shows some risk factors for cognitive decline. Recommended actions:
                • Consult with a healthcare provider
                • Increase cognitive exercises
                • Monitor memory changes
                • Consider lifestyle modifications
                • Regular medical check-ups
            """.trimIndent()
            
            RiskLevel.HIGH -> """
                Your assessment indicates higher risk factors. Important next steps:
                • Schedule an appointment with a neurologist
                • Comprehensive medical evaluation
                • Regular monitoring of symptoms
                • Family support system engagement
                • Consider medication evaluation
            """.trimIndent()
        }
        
        textViewRecommendations.text = recommendations
    }
    
    private fun shareResults() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Alzheimer's Assessment Results")
            putExtra(Intent.EXTRA_TEXT, """
                Alzheimer's Assessment Results:
                Risk Level: ${textViewRiskLevel.text}
                Memory Score: ${progressMemory.progress}%
                Cognitive Score: ${progressCognitive.progress}%
                
                ${textViewRecommendations.text}
            """.trimIndent())
        }
        startActivity(Intent.createChooser(shareIntent, "Share results via"))
    }
    
    private fun startOver() {
        // Clear saved data
        getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        
        // Return to first screen
        val intent = Intent(this, PatientDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    
    private enum class RiskLevel {
        LOW, MODERATE, HIGH
    }
} 