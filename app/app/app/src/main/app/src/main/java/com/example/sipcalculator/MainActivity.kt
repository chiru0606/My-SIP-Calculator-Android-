package com.example.sipcalculator

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

class MainActivity : Activity() {

    private lateinit var sipInput: EditText
    private lateinit var rateInput: EditText
    private lateinit var yearsInput: EditText

    private lateinit var investedResult: TextView
    private lateinit var profitResult: TextView
    private lateinit var totalResult: TextView

    private val green = Color.rgb(22, 120, 85)
    private val dark = Color.rgb(25, 40, 55)
    private val bg = Color.rgb(245, 248, 247)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = dark
        window.navigationBarColor = dark

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(bg)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(24))
        }

        scroll.addView(layout)
        setContentView(scroll)

        addText(layout, "SIP CALCULATOR", 27f, dark, true)
        addText(
            layout,
            "Plan your future wealth with smart investing",
            14f,
            Color.GRAY
        )

        addSpace(layout, 24)

        addText(layout, "Monthly Investment (₹)", 16f, dark, true)
        sipInput = addInput(layout, "Example: 5000", "5000")

        addSpace(layout, 16)

        addText(layout, "Expected Annual Return (%)", 16f, dark, true)
        rateInput = addInput(layout, "Example: 12", "12")

        addSpace(layout, 16)

        addText(layout, "Investment Period (Years)", 16f, dark, true)
        yearsInput = addInput(layout, "Example: 10", "10")

        addSpace(layout, 24)

        val calculateButton = Button(this).apply {
            text = "CALCULATE SIP"
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(green)
        }

        layout.addView(
            calculateButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(layout, 28)

        addText(layout, "YOUR INVESTMENT SUMMARY", 18f, dark, true)

        addSpace(layout, 14)

        val resultCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            setBackgroundColor(Color.WHITE)
        }

        layout.addView(
            resultCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        addText(resultCard, "Estimated Maturity Value", 14f, Color.GRAY)

        totalResult = addText(
            resultCard, "₹0", 30f, green, true
        )

        addSpace(resultCard, 20)

        addText(resultCard, "Total Amount Invested", 14f, Color.GRAY)

        investedResult = addText(
            resultCard, "₹0", 20f, dark, true
        )

        addSpace(resultCard, 16)

        addText(resultCard, "Estimated Returns", 14f, Color.GRAY)

        profitResult = addText(
            resultCard, "₹0", 20f, green, true
        )

        addSpace(layout, 24)

        addText(
            layout,
            "Note: This calculator provides illustrative estimates. " +
                "Actual mutual fund returns are market-linked and not guaranteed.",
            13f,
            Color.GRAY
        )

        calculateButton.setOnClickListener {
            calculateSIP()
        }

        calculateSIP()
    }

    private fun calculateSIP() {
        val monthly = sipInput.text.toString().toDoubleOrNull()
        val annualRate = rateInput.text.toString().toDoubleOrNull()
        val years = yearsInput.text.toString().toDoubleOrNull()

        if (monthly == null || annualRate == null || years == null ||
            monthly <= 0 || annualRate < 0 || years <= 0 ||
            years > 100
        ) {
            Toast.makeText(
                this,
                "Please enter valid investment details",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val months = (years * 12).toInt()

        if (months <= 0) return

        val monthlyRate = annualRate / 12 / 100

        val maturity: Double

        if (monthlyRate == 0.0) {
            maturity = monthly * months
        } else {
            maturity = monthly *
                ((1 + monthlyRate).pow(months) - 1) /
                monthlyRate
        }

        val invested = monthly * months
        val profit = maturity - invested

        investedResult.text = formatRupees(invested)
        profitResult.text = formatRupees(profit)
        totalResult.text = formatRupees(maturity)
    }

    private fun formatRupees(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(
            Locale("en", "IN")
        )
        formatter.maximumFractionDigits = 0
        return formatter.format(amount)
    }

    private fun addInput(
        parent: LinearLayout,
        hintText: String,
        defaultValue: String
    ): EditText {

        val input = EditText(this).apply {
            hint = hintText
            setText(defaultValue)
            textSize = 17f
            setSingleLine(true)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            setBackgroundColor(Color.WHITE)
        }

        parent.addView(
            input,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        return input
    }

    private fun addText(
        parent: LinearLayout,
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {

        val view = TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)

            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
        }

        parent.addView(view)
        return view
    }

    private fun addSpace(parent: LinearLayout, height: Int) {
        val space = TextView(this)
        parent.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        )
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
