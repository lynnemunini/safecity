package com.grayseal.safecity.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grayseal.safecity.ui.theme.Green
import com.grayseal.safecity.ui.theme.poppinsFamily

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrimeReportForm() {
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
    var evidence by remember { mutableStateOf("") }
    var otherInformation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Incident details
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Column {
                androidx.compose.material3.Text(
                    "Please provide the details of the incident, " +
                            "including the date and time it occurred, as well as the " +
                            "specific location where it took place.",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontFamily = poppinsFamily,
                    fontSize = 12.sp
                )
                androidx.compose.material3.OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    placeholder = { androidx.compose.material3.Text("Time") },
                    modifier = Modifier.fillMaxWidth(),
                )
                androidx.compose.material3.OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    placeholder = { androidx.compose.material3.Text("Date") },
                    readOnly = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.material3.OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { androidx.compose.material3.Text("Location") },
                    modifier = Modifier.fillMaxWidth()
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
            Column {
                androidx.compose.material3.Text(
                    "Please provide a brief description of the type of crime that " +
                            "occurred, such as theft, burglary, or assault",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontFamily = poppinsFamily,
                    fontSize = 12.sp
                )
                androidx.compose.material3.OutlinedTextField(
                    value = crimeDescription,
                    onValueChange = { crimeDescription = it },
                    placeholder = { androidx.compose.material3.Text("Crime description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        // Victim information
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Column {
                androidx.compose.material3.Text(
                    "Please provide the victim's name, ID number (if available), and " +
                            "contact information (such as phone number or email address).",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontFamily = poppinsFamily,
                    fontSize = 12.sp
                )
                androidx.compose.material3.OutlinedTextField(
                    value = victimID,
                    onValueChange = { victimID = it },
                    placeholder = { androidx.compose.material3.Text("Victim ID no.") },
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.material3.OutlinedTextField(
                    value = victimName,
                    onValueChange = { victimName = it },
                    placeholder = { androidx.compose.material3.Text("Victim name") },
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.material3.OutlinedTextField(
                    value = victimContact,
                    onValueChange = { victimContact = it },
                    placeholder = { androidx.compose.material3.Text("Victim contact information") },
                    modifier = Modifier.fillMaxWidth()
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
            Column {
                androidx.compose.material3.Text(
                    "Please provide any details you have about the suspect(s), including " +
                            "their physical description, name (if known), and any identifying " +
                            "features such as tattoos or scars.",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontFamily = poppinsFamily,
                    fontSize = 12.sp
                )
                androidx.compose.material3.OutlinedTextField(
                    value = suspectName,
                    onValueChange = { suspectName = it },
                    placeholder = { androidx.compose.material3.Text("Suspect name") },
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.material3.OutlinedTextField(
                    value = suspectDescription,
                    onValueChange = { suspectDescription = it },
                    placeholder = { androidx.compose.material3.Text("Suspect description") },
                    modifier = Modifier.fillMaxWidth()
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
            Column {
                androidx.compose.material3.Text(
                    "Please provide the name(s) and contact information (such as phone " +
                            "number or email address) of any witnesses to the crime. " +
                            "Additionally, you may provide a brief description of what the witness(es) saw.",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontFamily = poppinsFamily,
                    fontSize = 12.sp
                )
                androidx.compose.material3.OutlinedTextField(
                    value = witnessName,
                    onValueChange = { witnessName = it },
                    placeholder = { androidx.compose.material3.Text("Witness name") },
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.material3.OutlinedTextField(
                    value = witnessContact,
                    onValueChange = { witnessContact = it },
                    placeholder = { androidx.compose.material3.Text("Witness contact information") },
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.material3.TextField(
                    value = witnessDescription,
                    onValueChange = { witnessDescription = it },
                    placeholder = { androidx.compose.material3.Text("Brief description") },
                    modifier = Modifier.fillMaxWidth()
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
            Column {
                androidx.compose.material3.Text(
                    "Please provide any evidence or additional information related to the " +
                            "crime. This could include photos, videos, or any other relevant documents.",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontFamily = poppinsFamily,
                    fontSize = 12.sp
                )
                androidx.compose.material3.OutlinedTextField(
                    value = evidence,
                    onValueChange = { evidence = it },
                    placeholder = { androidx.compose.material3.Text("Evidence") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Other information
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Column {
                androidx.compose.material3.OutlinedTextField(
                    value = otherInformation,
                    onValueChange = { otherInformation = it },
                    placeholder = {
                        androidx.compose.material3.Text(
                            "If you have any other information related to the crime that you " +
                                    "think may be helpful, please provide it below.",
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontFamily = poppinsFamily,
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        // Submit button
        androidx.compose.material3.Button(
            onClick = { submitReport() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green)
        ) {
            androidx.compose.material3.Text("Submit")
        }
    }
}

fun submitReport() {
    // TODO: Implement the logic for submitting the crime report
}
