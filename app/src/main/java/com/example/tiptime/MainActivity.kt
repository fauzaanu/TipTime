package com.example.tiptime
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection.Companion.Down
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat.FocusDirection
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TipTimeScreen()
                }
            }
        }
    }
}
@Composable
fun TipTimeScreen(){

    // we are tracking this variable for changes
    var inputText by remember {
        mutableStateOf("")
    }

    var inputTip by remember {
        mutableStateOf("")
    }

    var roundUp by remember {
        mutableStateOf(false)
    }

    // some simple math
    val amount = inputText.toDoubleOrNull() ?: 0.0
    val tip_p = inputTip.toDoubleOrNull() ?: 0.0
    val tip = calculateTip(amount,tipPercent=tip_p,roundUp)

    val focusManager = LocalFocusManager.current
    // main stuff
    Column(modifier = Modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = R.string.calculate_tip),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // input text us passed to the edit number field as a value and on change val
        // the it keyword refers to the item that is passed
        // in this case it should be inputText var
        // on the other side the edit number field is expecting a function that takes a string
        // and returns nothing (Unit)
        // the main onValueCHnage on the otehrside expects a function

        EditNumberField(
            label =R.string.bill_amount,
            value = inputText,
            onValueChange = {inputText = it},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(Down)}
            )
        )

        EditNumberField(
            label =R.string.how_was_the_service,
            value = inputTip,
            onValueChange = {inputTip = it},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus()}
            )
        )
        RoundTheTipRow(roundUp = roundUp, onRoundUpChange = {roundUp = it})
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.tip_amount, tip),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
        )
    }
}

@Composable
fun RoundTheTipRow(
    roundUp: Boolean,
    onRoundUpChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.round_up_tip))
        Switch(checked = roundUp,
            onCheckedChange = onRoundUpChange,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            colors = SwitchDefaults.colors(uncheckedThumbColor = Color.DarkGray)
        )
    }
}





@Composable
fun EditNumberField(
    @StringRes label: Int,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
){
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(text = stringResource(id = label), modifier = Modifier.fillMaxWidth()) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}


private fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundUp: Boolean): String{
    var tip = tipPercent / 100 * amount

    if(roundUp){
        tip = kotlin.math.ceil(tip)
    }
    return NumberFormat.getCurrencyInstance().format(tip)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipTimeTheme {
        TipTimeScreen()
    }
}