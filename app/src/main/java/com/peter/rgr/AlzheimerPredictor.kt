package com.peter.rgr

class AlzheimerPredictor {
    companion object {
        fun calculateRisk(
            age: Int,
            gender: Int, // 0 for Male, 1 for Female
            education: Int, // Years of education
            socioEconomicStatus: Int, // 1-5 scale
            mmseScore: Int, // Mini-Mental State Examination score (0-30)
            cdScore: Int, // Clinical Dementia Rating (0-3)
            memoryScore: Int, // Memory assessment score
            orientationScore: Int, // Orientation test score
            visualScore: Int, // Visual memory test score
            cognitiveScore: Int // Cognitive assessment score
        ): PredictionResult {
            // Normalize inputs based on your model's training data ranges
            val normalizedAge = normalizeAge(age)
            val normalizedEducation = normalizeEducation(education)
            val normalizedMMSE = normalizeMMSE(mmseScore)
            
            // Calculate composite scores
            val memoryIndex = calculateMemoryIndex(memoryScore, orientationScore)
            val cognitiveIndex = calculateCognitiveIndex(visualScore, cognitiveScore)
            
            // Apply logistic regression (coefficients should match your trained model)
            val probability = calculateProbability(
                normalizedAge,
                gender.toDouble(),
                normalizedEducation,
                socioEconomicStatus.toDouble(),
                normalizedMMSE,
                cdScore.toDouble(),
                memoryIndex,
                cognitiveIndex
            )
            
            // Determine risk level
            val riskLevel = when {
                probability >= 0.7 -> RiskLevel.HIGH
                probability >= 0.3 -> RiskLevel.MODERATE
                else -> RiskLevel.LOW
            }
            
            return PredictionResult(
                probability = probability,
                riskLevel = riskLevel,
                recommendations = generateRecommendations(riskLevel, memoryIndex, cognitiveIndex)
            )
        }
        
        private fun normalizeAge(age: Int): Double {
            // Normalize age to 0-1 range (assuming age range 40-90 from training data)
            return (age - 40.0) / (90.0 - 40.0)
        }
        
        private fun normalizeEducation(years: Int): Double {
            // Normalize education years to 0-1 range (assuming range 0-20 years)
            return years.toDouble() / 20.0
        }
        
        private fun normalizeMMSE(score: Int): Double {
            // Normalize MMSE score to 0-1 range (0-30 scale)
            return score.toDouble() / 30.0
        }
        
        private fun calculateMemoryIndex(memoryScore: Int, orientationScore: Int): Double {
            // Combine memory and orientation scores (0-100 scale)
            return (memoryScore * 0.6 + orientationScore * 0.4)
        }
        
        private fun calculateCognitiveIndex(visualScore: Int, cognitiveScore: Int): Double {
            // Combine visual and cognitive scores (0-100 scale)
            return (visualScore * 0.5 + cognitiveScore * 0.5)
        }
        
        private fun calculateProbability(
            age: Double,
            gender: Double,
            education: Double,
            ses: Double,
            mmse: Double,
            cd: Double,
            memoryIndex: Double,
            cognitiveIndex: Double
        ): Double {
            // Logistic regression coefficients (replace with your model's coefficients)
            val coefficients = mapOf(
                "age" to 0.35,
                "gender" to -0.15,
                "education" to -0.25,
                "ses" to -0.2,
                "mmse" to -0.4,
                "cd" to 0.45,
                "memory" to -0.3,
                "cognitive" to -0.3
            )
            
            // Calculate logistic regression
            val z = coefficients["age"]!! * age +
                    coefficients["gender"]!! * gender +
                    coefficients["education"]!! * education +
                    coefficients["ses"]!! * ses +
                    coefficients["mmse"]!! * mmse +
                    coefficients["cd"]!! * cd +
                    coefficients["memory"]!! * memoryIndex +
                    coefficients["cognitive"]!! * cognitiveIndex
            
            // Apply sigmoid function
            return 1.0 / (1.0 + Math.exp(-z))
        }
        
        private fun generateRecommendations(
            riskLevel: RiskLevel,
            memoryIndex: Double,
            cognitiveIndex: Double
        ): List<String> {
            val recommendations = mutableListOf<String>()
            
            when (riskLevel) {
                RiskLevel.LOW -> {
                    recommendations.add("Continue regular cognitive exercises")
                    recommendations.add("Maintain healthy lifestyle habits")
                    recommendations.add("Regular check-ups with healthcare provider")
                }
                RiskLevel.MODERATE -> {
                    recommendations.add("Schedule consultation with neurologist")
                    recommendations.add("Increase frequency of cognitive exercises")
                    recommendations.add("Consider memory enhancement activities")
                    if (memoryIndex < 70) {
                        recommendations.add("Focus on memory training exercises")
                    }
                    if (cognitiveIndex < 70) {
                        recommendations.add("Engage in problem-solving activities")
                    }
                }
                RiskLevel.HIGH -> {
                    recommendations.add("Immediate consultation with specialist")
                    recommendations.add("Comprehensive neurological evaluation")
                    recommendations.add("Consider medication evaluation")
                    recommendations.add("Family support system engagement")
                    recommendations.add("Regular monitoring of symptoms")
                }
            }
            
            return recommendations
        }
    }
    
    enum class RiskLevel {
        LOW, MODERATE, HIGH
    }
    
    data class PredictionResult(
        val probability: Double,
        val riskLevel: RiskLevel,
        val recommendations: List<String>
    )
} 