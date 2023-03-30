package com.grayseal.safecity.screens.report

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.grayseal.safecity.BuildConfig.*
import com.grayseal.safecity.R
import com.grayseal.safecity.navigation.Screen
import com.grayseal.safecity.sms.sendMessage
import com.grayseal.safecity.ui.theme.Green
import com.grayseal.safecity.ui.theme.poppinsFamily
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController, name: String?) {
    var time by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var crimeDescription by remember { mutableStateOf("") }
    var victimName by remember { mutableStateOf("") }
    var victimID by remember { mutableStateOf("") }
    var victimContact by remember { mutableStateOf("") }
    var suspectName by remember { mutableStateOf("") }
    var suspectDescription by remember { mutableStateOf("") }
    var witnessName by remember { mutableStateOf("") }
    var witnessContact by remember { mutableStateOf("") }
    var witnessDescription by remember { mutableStateOf("") }
    var otherInformation by remember { mutableStateOf("") }

    // Types
    val crimes = listOf("Theft", "Burglary", "Assault", "Vandalism", "Fraud")
    val context = LocalContext.current
    var reporting by remember {
        mutableStateOf(false)
    }

    // CALENDAR
    val cal = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            date = selectedDate // set the selected date to the mutableStateOf variable
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    // TIME PICKER
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            cal.set(Calendar.HOUR_OF_DAY, selectedHour)
            cal.set(Calendar.MINUTE, selectedMinute)
            val selectedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
            time = selectedTime // set the selected time to the mutableStateOf variable
        },
        hour,
        minute,
        DateFormat.is24HourFormat(context)
    )

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    // Retrieve an image from the device gallery
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        // take a persistable URI permission to access the content of the URI outside of the scope of app's process
        val contentResolver = context.contentResolver
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                Toast.makeText(
                    context,
                    "Failed to take permission: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        imageUri = uri
    }

    imageUri?.let {
        if (Build.VERSION.SDK_INT < 28) {
            bitmap.value = MediaStore.Images
                .Media.getBitmap(context.contentResolver, it)

        } else {
            val source = ImageDecoder
                .createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }
    }

    Box {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = {
                            navController.popBackStack()
                        })
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Report a crime",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text(
                        name.toString(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            // Incident details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    Text(
                        "Please provide the details of the incident, " +
                                "including the date and time it occurred, as well as the " +
                                "specific location where it took place.",
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        placeholder = {
                            Text(
                                "Time of Crime",
                            )
                        },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                timePickerDialog.show()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_time),
                                    "Time",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        },
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        placeholder = { Text("Date of Crime") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                datePickerDialog.show()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_calendar),
                                    "Calendar",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        },
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("Location of Crime") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }
            // Crime description
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    Text(
                        "Please provide a brief description of the type of crime that " +
                                "occurred, such as theft, burglary, or assault",
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        crimes.forEach { crime ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = crimeDescription == crime,
                                    onClick = { crimeDescription = crime },
                                    interactionSource = MutableInteractionSource(),
                                    colors = RadioButtonDefaults.colors(selectedColor = Green)
                                )
                                Text(
                                    text = crime,
                                    modifier = Modifier.padding(start = 8.dp),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
            // Victim information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    Text(
                        "Please provide the victim's name, ID number (if available), and " +
                                "contact information (such as phone number or email address).",
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = victimID,
                        onValueChange = { victimID = it },
                        placeholder = { Text("Victim National ID no.") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        )
                    )
                    OutlinedTextField(
                        value = victimName,
                        onValueChange = { victimName = it },
                        placeholder = { Text("Victim name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                    OutlinedTextField(
                        value = victimContact,
                        onValueChange = { victimContact = it },
                        placeholder = { Text("Victim contact information (+2547...)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }
            // Suspect information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    Text(
                        "Please provide any details you have about the suspect(s), including " +
                                "their physical description, name (if known), and any identifying " +
                                "features such as tattoos or scars.",
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = suspectName,
                        onValueChange = { suspectName = it },
                        placeholder = { Text("Suspect name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                    OutlinedTextField(
                        value = suspectDescription,
                        onValueChange = { suspectDescription = it },
                        placeholder = { Text("Suspect description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }
            // Witness information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    Text(
                        "Please provide the name(s) and contact information (such as phone " +
                                "number or email address) of any witnesses to the crime. " +
                                "Additionally, you may provide a brief description of what the witness(es) saw.",
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = witnessName,
                        onValueChange = { witnessName = it },
                        placeholder = { Text("Witness name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                    OutlinedTextField(
                        value = witnessContact,
                        onValueChange = { witnessContact = it },
                        placeholder = { Text("Witness contact information") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                    OutlinedTextField(
                        value = witnessDescription,
                        onValueChange = { witnessDescription = it },
                        placeholder = { Text("Brief description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }
            // Evidence
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Please provide any evidence or additional information related to the " +
                                "crime. This could include photos, videos, or any other relevant documents.",
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp
                    )
                    // Show file name if file is selected
                    if (imageUri != null) {
                        Text("Selected file: ${imageUri?.path}")
                        bitmap.value?.asImageBitmap()?.let {
                            Image(
                                bitmap = it,
                                contentDescription = imageUri?.path,
                                modifier = Modifier.height(300.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    // Button to select file
                    TextButton(
                        onClick = {
                            launcher.launch(arrayOf("image/*"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            "Select File",
                            fontFamily = poppinsFamily,
                            fontSize = 16.sp,
                            color = Green,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            // Other information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    Text(
                        "If you have any other information related to the crime that you " +
                                "think may be helpful, please provide it below.",
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = otherInformation,
                        onValueChange = { otherInformation = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Green,
                            focusedBorderColor = Green
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }
            // Submit button
            Button(
                onClick = {
                    reporting = true
                    submitReport(
                        navController = navController,
                        policeStationName = name.toString(),
                        time = time,
                        date = date,
                        location = location,
                        type = crimeDescription,
                        victimId = victimID,
                        victimName = victimName,
                        victimContact = victimContact,
                        suspectName = suspectName,
                        suspectDescription = suspectDescription,
                        witnessName = witnessName,
                        witnessContact = witnessContact,
                        description = witnessDescription,
                        evidence = imageUri.toString(),
                        otherInformation = otherInformation
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 10.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green)
            ) {
                Text(
                    "REPORT",
                    fontFamily = poppinsFamily,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if (reporting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Green)
            }
        }
    }
}

fun submitReport(
    navController: NavController,
    policeStationName: String,
    time: String,
    date: String,
    location: String,
    type: String,
    victimId: String,
    victimName: String,
    victimContact: String,
    suspectName: String,
    suspectDescription: String,
    witnessName: String,
    witnessContact: String,
    description: String,
    evidence: String,
    otherInformation: String
) {
    val db = Firebase.firestore
    val report = hashMapOf(
        "time" to time,
        "date" to date,
        "location" to location,
        "typeOfCrime" to type,
        "victimId" to victimId,
        "victimName" to victimName,
        "victimContact" to victimContact,
        "suspectName" to suspectName,
        "suspectDescription" to suspectDescription,
        "witnessName" to witnessName,
        "witnessContact" to witnessContact,
        "description" to description,
        "evidence" to evidence,
        "otherInformation" to otherInformation
    )
    db.collection("reports")
        .add(report)
        .addOnSuccessListener {
            navController.navigate(Screen.ReportSubmitScreen.route)
            sendMessage(victimName, victimContact, policeStationName)
        }
}
