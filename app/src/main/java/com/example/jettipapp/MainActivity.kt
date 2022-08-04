package com.example.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.ui.theme.Purple200
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconButton
import java.util.*

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                Column {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .padding(start = 20.dp, top = 20.dp, end = 20.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Purple200) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(Locale.ENGLISH, totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.h4
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun MainContent() {
    val amountOfPeopleToSplitState = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    BillForm(
        amountOfPeopleToSplitState = amountOfPeopleToSplitState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    )
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(modifier: Modifier = Modifier,
             range: IntRange = 1..100,
             amountOfPeopleToSplitState: MutableState<Int>,
             tipAmountState: MutableState<Double>,
             totalPerPersonState: MutableState<Double>,
             onValChanged: (String) -> Unit = {}
             ) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    TopHeader(totalPerPersonState.value)
    Surface(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        border = BorderStroke(1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChanged(totalBillState.value.trim())
                    keyboardController?.hide()
                })
            if (validState) {
                Row(modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                amountOfPeopleToSplitState.value =
                                    if (amountOfPeopleToSplitState.value > 1) amountOfPeopleToSplitState.value - 1 else 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = amountOfPeopleToSplitState.value,
                                        tipPercentage = tipPercentage
                                    )
                            }
                        )
                        Text(
                            text = amountOfPeopleToSplitState.value.toString(),
                            modifier = Modifier
                                .padding(vertical = 5.dp, horizontal = 10.dp)
                                .align(Alignment.CenterVertically),
                            fontSize = 20.sp
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (amountOfPeopleToSplitState.value < range.last) amountOfPeopleToSplitState.value++
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = amountOfPeopleToSplitState.value,
                                        tipPercentage = tipPercentage
                                    )
                            }
                        )
                    }
                }
            Row(modifier = Modifier
                .padding(
                    horizontal = 3.dp,
                    vertical = 12.dp
                )) {
                Text(
                    text = "Tip",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(200.dp))
                Text(
                    text = "${tipAmountState.value}",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "$tipPercentage%")
                Spacer(modifier = Modifier.height(14.dp))
                Slider(
                    value = sliderPositionState.value,
                    onValueChange = { newVal ->
                        sliderPositionState.value = newVal
                        tipAmountState.value =
                        calculateTotalTip(
                            totalBill = totalBillState.value.toDouble(),
                            tipPercentage = tipPercentage
                        )
                        Log.d("XXX", "tip ${tipAmountState.value} totalBill ${totalBillState.value.toDouble()} tip perenctage $tipPercentage")
                        totalPerPersonState.value =
                            calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = amountOfPeopleToSplitState.value,
                                tipPercentage = tipPercentage
                            )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    steps = 5
                )
            }
            } else {
                Box {}
            }
        }
    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {
        MyApp {
            Column {
                MainContent()
            }
        }
    }
}