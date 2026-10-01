package com.example.domain

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceNotifier(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            e.printStackTrace()
            tts = null
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val result = tts?.setLanguage(Locale("pt", "BR"))
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isReady = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun speakEvaluation(status: String, ratePerKm: Double, netProfit: Double) {
        if (!isReady || tts == null) return
        try {
            val statusText = when (status) {
                "ACEITAR" -> "Aceitar! Corrida lucrativa."
                "ATENÇÃO" -> "Atenção! Corrida na média."
                else -> "Recusar! Prejuízo à vista."
            }
            val rateText = String.format(Locale("pt", "BR"), "%.2f", ratePerKm).replace(".", " vírgula ")
            val profitText = String.format(Locale("pt", "BR"), "%.2f", netProfit).replace(".", " vírgula ")
            val message = "$statusText $rateText reais por quilômetro. Lucro de $profitText reais."
            tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "semaforo_eval")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
