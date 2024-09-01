package com.example.myfittracker.presentation.screens

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel
import com.example.myfittracker.presentation.viewmodel.ViewModelManager
import com.example.myfittracker.R
import java.util.Calendar
import java.util.Date
import java.util.Locale

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            FitnessDashboard()
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessDashboard(
    macAddress: String,
    sharedDevicesScreenViewModel: SharedDevicesScreenViewModel,
) {
    val viewModel = ViewModelManager.getViewModel(macAddress)
    val heartRateGraphData = viewModel?.heartRateGraphData?.observeAsState(emptyList())?.value

    val deviceName = sharedDevicesScreenViewModel
        .discoveredDevicesMap
        .observeAsState().value?.get(macAddress) ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite, // Example icon
                            contentDescription = "Dashboard Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp).padding(end = 8.dp)
                        )
                        Text(
                            text = "Fitness Dashboard : $deviceName",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle action */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF3A7BD5), Color(0xFF00D2FF))
                        )
                    )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Data") },
                    label = { Text("Data") },
                    selected = false,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {}
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp) // Optional: to add some padding at the end
        ) {
            item {
                FitnessParameters(macAddress)
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                FitnessGraphs(heartRateGraphData ?: emptyList(), macAddress)
            }
        }
    }
}



//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FitnessDashboard(
//    macAddress: String,
//    sharedDevicesScreenViewModel: SharedDevicesScreenViewModel,
//) {
//    val viewModel = ViewModelManager.getViewModel(macAddress)
//    val heartRateGraphData = viewModel?.heartRateGraphData?.observeAsState(emptyList())?.value
//
//    val deviceName = sharedDevicesScreenViewModel
//        .discoveredDevicesMap
//        .observeAsState().value?.get(macAddress) ?: ""
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Fitness Dashboard : $deviceName") }
//            )
//        },
//        bottomBar = {
//            NavigationBar {
//                NavigationBarItem(
//                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
//                    label = { Text("Home") },
//                    selected = true,
//                    onClick = {}
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.Filled.List, contentDescription = "Data") },
//                    label = { Text("Data") },
//                    selected = false,
//                    onClick = {}
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
//                    label = { Text("Settings") },
//                    selected = false,
//                    onClick = {}
//                )
//            }
//        }
//    ) { innerPadding ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(16.dp),
//            contentPadding = PaddingValues(bottom = 16.dp) // Optional: to add some padding at the end
//        ) {
//            item {
//                FitnessParameters(macAddress)
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//            item {
//                FitnessGraphs(heartRateGraphData ?: emptyList(), macAddress)
//            }
//        }
//
//    }
//}


@Composable
fun FitnessParameters(
    macAddress: String
) {
    val viewModel = ViewModelManager.getViewModel(macAddress)

    val heartRate = viewModel?.heartRate?.observeAsState()?.value
    val temperature = viewModel?.temperature?.observeAsState()?.value
    val bloodDBP = viewModel?.bloodDBP?.observeAsState()?.value
    val bloodSBP = viewModel?.bloodSBP?.observeAsState()?.value

    // Using a grid layout with two columns
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp) // Adding some padding to the row
                .height(180.dp),
            horizontalArrangement = Arrangement.SpaceBetween // Use SpaceBetween to ensure even spacing
        ) {
            FitnessParameterCard(
                title = "Heart Rate",
                value = "${heartRate ?: "..."} BPM",
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(40.dp)
                    )
                },
                modifier = Modifier.weight(1.5f)
            )
            Spacer(modifier = Modifier.width(4.dp)) // Add a spacer between the cards
            FitnessParameterCard(
                title = "Temperature",
                value = temperature ?: "...",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.thermometer_24dp_ff6a00_fill1_wght400_grad0_opsz24),
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(40.dp)
                    )
                },
                modifier = Modifier.weight(1.5f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                //.padding(horizontal = 2.dp) // Adding some padding to the row
                .height(180.dp),
            horizontalArrangement = Arrangement.SpaceBetween // Use SpaceBetween to ensure even spacing
        ) {
            FitnessParameterCard(
                title = "Blood Pressure",
                value = "$bloodDBP/$bloodSBP" ?: "...",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.blood_pressure_24dp_ff6a00_fill1_wght400_grad0_opsz24),
                        contentDescription = null,
                        tint = Color(0xFF0BE1D1),
                        modifier = Modifier.size(40.dp)
                    )
                },
                modifier = Modifier.weight(1.5f)
            )
            Spacer(modifier = Modifier.width(4.dp)) // Add a spacer between the cards
            FitnessParameterCard(
                title = "Calories",
                value = "500 kcal",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.local_fire_department_24dp_ff6a00_fill1_wght400_grad0_opsz24),
                        contentDescription = null,
                        tint = Color(0xFFF18027),
                        modifier = Modifier.size(40.dp)
                    )
                },
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}


@Composable
fun FitnessParameterCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxHeight() // Fill the height available in the Row
            .fillMaxWidth(), // Ensure it takes up available width
        shape = MaterialTheme.shapes.large, // Use larger rounded corners
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)), // Soft background color
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Increase shadow
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp) // Larger icon size
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFEDE7F6), Color(0xFFBBDEFB))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.height(12.dp)) // Increased space between icon and text
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF555555) // Darker color for the title
            )
            Spacer(modifier = Modifier.height(4.dp)) // Slightly less space between title and value
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333) // Bold color for the value
            )
        }
    }
}

@Composable
fun FitnessGraphs(heartRateGraphData: List<Int>, macAddress: String) {
    val viewModel = ViewModelManager?.getViewModel(macAddress)
    val time = viewModel?.heartStartTime ?: "00:00"
    val timestamps = generateTimestamps(heartRateGraphData.size, time)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Heart Rate Graph", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        HeartRateGraph(heartRateGraphData, timestamps)

        Spacer(modifier = Modifier.height(35.dp))

        Text("Step Count Graph", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        StepCountGraph()
    }
}

fun generateTimestamps(count: Int, startTime: String?): List<String> {

    //val startTimeHearth = startTime?.substring(11, 16)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timestamps = mutableListOf<String>()

    // Start from the current time
    val calendar = Calendar.getInstance()

    if (startTime != null && startTime.length >= 16) {
        try {
            val startTimeHearth = startTime?.substring(11, 16)
            startTimeHearth?.let {
                val parsedDate = format.parse(it)
                calendar.time = parsedDate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
//    startTimeHearth?.let {
//        val parsedDate = format.parse(it)
//        calendar.time = parsedDate
//    }

    for (i in 0 until count) {
        timestamps.add(format.format(Date(calendar.timeInMillis)))
        calendar.add(Calendar.MINUTE, 1) // Increment by 1 minute
    }

    return timestamps
}

//@Composable
//fun HeartRateGraph(heartRateGraphData: List<Int>) {
//    //val heartRateData = listOf(72, 75, 78, 77, 74, 73, 76, 79, 81, 78)
//    if (heartRateGraphData.isEmpty()) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//                .background(Color.LightGray),
//            contentAlignment = Alignment.Center
//        ) {
//            Text("No Heart Rate Data", color = Color.DarkGray)
//        }
//    } else {
//        Canvas(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//                .background(Color.LightGray)
//        ) {
//            val maxY = size.height
//            val maxX = size.width
//            val stepX = maxX / (heartRateGraphData.size - 1)
//            val stepY = maxY / (heartRateGraphData.maxOrNull()?.toFloat() ?: 1f)
//
//            val path = Path().apply {
//                moveTo(0f, maxY - heartRateGraphData[0] * stepY)
//                for (i in 1 until heartRateGraphData.size) {
//                    lineTo(i * stepX, maxY - heartRateGraphData[i] * stepY)
//                }
//            }
//
//            // Draw the graph line
//            drawPath(
//                path = path,
//                color = Color.Blue,
//                style = Stroke(width = 4f)
//            )
//
//            // Draw the data points and labels
//            for (i in heartRateGraphData.indices) {
//                val x = i * stepX
//                val y = maxY - heartRateGraphData[i] * stepY
//                drawCircle(
//                    Color.Red,
//                    radius = 5f,
//                    center = androidx.compose.ui.geometry.Offset(x, y)
//                )
//                drawContext.canvas.nativeCanvas.drawText(
//                    heartRateGraphData[i].toString(),
//                    x,
//                    y - 10,
//                    android.graphics.Paint().apply {
//                        color = android.graphics.Color.BLACK
//                        textSize = 24f
//                    }
//                )
//            }
//        }
//    }
//}
@Composable
fun HeartRateGraph(heartRateGraphData: List<Int>, timestamps: List<String>) {
    if (heartRateGraphData.isEmpty() || timestamps.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("No Heart Rate Data", color = Color.DarkGray)
        }
    } else {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.LightGray, Color.White)
                    )
                )
        ) {
            val maxY = size.height - 50.dp.toPx()  // Reserve space for timestamps
            val maxX = size.width
            val stepX = maxX / (heartRateGraphData.size - 1).coerceAtLeast(1)
            val stepY = maxY / (heartRateGraphData.maxOrNull()?.toFloat() ?: 1f)

            val path = Path().apply {
                moveTo(0f, maxY - heartRateGraphData[0] * stepY)
                for (i in 1 until heartRateGraphData.size) {
                    lineTo(i * stepX, maxY - heartRateGraphData[i] * stepY)
                }
            }

            // Draw the graph line with a smoother stroke
            drawPath(
                path = path,
                color = Color(0xFF3A86FF),
                style = Stroke(width = 6f)
            )

            // Draw the data points and labels
            for (i in heartRateGraphData.indices) {
                val x = i * stepX
                val y = maxY - heartRateGraphData[i] * stepY
                drawCircle(
                    Color.Red,
                    radius = 8f,
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )

                // Draw the heart rate value above the point
                drawContext.canvas.nativeCanvas.drawText(
                    heartRateGraphData[i].toString(),
                    x,
                    y - 15,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )

                // Draw the timestamp below the point
                drawContext.canvas.nativeCanvas.drawText(
                    timestamps[i],
                    x,
                    size.height - 10.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

@Composable
fun StepCountGraph() {
    val stepCountData = listOf(1000, 3000, 5000, 4000, 6000, 8000, 7000, 9000, 10000, 9500)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.LightGray)
    ) {
        val maxY = size.height
        val maxX = size.width
        val barWidth = maxX / stepCountData.size
        val stepY = maxY / (stepCountData.maxOrNull()?.toFloat() ?: 1f)

        for (i in stepCountData.indices) {
            val x = i * barWidth
            val y = maxY - stepCountData[i] * stepY
            drawRect(
                color = Color.Green,
                topLeft = androidx.compose.ui.geometry.Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth - 4.dp.toPx(), stepCountData[i] * stepY)
            )

            // Draw the data labels
            drawContext.canvas.nativeCanvas.drawText(
                stepCountData[i].toString(),
                x + barWidth / 4,
                y - 10,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 24f
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun FitnessDashboardPreview() {

    val mockHeartRateData = listOf(72, 75, 78, 80, 76, 74)
    val mockTimestamps = generateTimestamps(mockHeartRateData.size, "2024-08-31 12:12:00")
    FitnessGraphs(mockHeartRateData, "#$#$#SDDADADA")
    FitnessDashboard(
        "#$#$#SDDADADA",
        sharedDevicesScreenViewModel = SharedDevicesScreenViewModel()
    )
}

//@Preview(
//    showBackground = true, // Shows a background to better visualize the composable
//    showSystemUi = true    // Mimics the system UI elements like status bar
//)
//@Composable
//fun FitnessParameterCardPreview() {
//    // You can pass mock data here to preview the composable
//    FitnessParameterCard(
//        title = "Heart Rate", // Mock title
//        value = "75 BPM",     // Mock value
//        icon = {
//            Icon(
//                imageVector = Icons.Rounded.Favorite, // Mock icon
//                contentDescription = null,
//                tint = Color.Red,
//                modifier = Modifier.size(24.dp)
//            )
//        }
//    )
//}

//@Preview(
//    showBackground = true, // Shows a background to better visualize the composable
//    showSystemUi = true    // Mimics the system UI elements like status bar
//)
//@Composable
//fun FitnessParametersPreview() {
//    // You can pass a mock macAddress or any string, since the ViewModel isn't actually used in preview
//    FitnessParameters(
//        macAddress = "00:00:00:00:00:00" // Mock MAC address
//    )
//}




