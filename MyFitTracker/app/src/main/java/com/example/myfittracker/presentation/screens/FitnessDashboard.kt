package com.example.myfittracker.presentation.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

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
fun FitnessDashboard() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fitness Dashboard") }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            FitnessParameters()
            Spacer(modifier = Modifier.height(16.dp))
            FitnessGraphs()
        }
    }
}

@Composable
fun FitnessParameters() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FitnessParameterCard(
            "Heart Rate",
            "72 BPM"
        ) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(100.dp)
            )
        }
        FitnessParameterCard(
            "Steps",
            "10,000"
        ){
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(40.dp)
            )
        }
        FitnessParameterCard(
            "SPO2",
            "98%"
        ){
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(40.dp)
            )
        }
        FitnessParameterCard(
            "Calories",
            "500 kcal"
        ){
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

//@Composable
//fun FitnessParameterCard(title: String, value: String) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Canvas(
//            modifier = Modifier
//                .size(80.dp)
//                .background(Color.LightGray, CircleShape)
//        ) {
//            drawCircle(
//                color = Color.Blue,
//                style = Stroke(width = 8f)
//            )
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = title, fontWeight = FontWeight.Bold)
//        Text(text = value, fontSize = 20.sp)
//    }
//}
@Composable
fun FitnessParameterCard(title: String, value: String, icon: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                drawCircle(
                    color = Color.Blue,
                    style = Stroke(width = 8f)
                )
            }
            // Place the icon inside the circle
            icon()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 20.sp)
    }
}

@Composable
fun FitnessGraphs() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Heart Rate Graph", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        HeartRateGraph()

        Spacer(modifier = Modifier.height(16.dp))

        Text("Step Count Graph", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        StepCountGraph()
    }
}

@Composable
fun HeartRateGraph() {
    val heartRateData = listOf(72, 75, 78, 77, 74, 73, 76, 79, 81, 78)

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .background(Color.LightGray)
    ) {
        val maxY = size.height
        val maxX = size.width
        val stepX = maxX / (heartRateData.size - 1)
        val stepY = maxY / (heartRateData.maxOrNull()?.toFloat() ?: 1f)

        val path = Path().apply {
            moveTo(0f, maxY - heartRateData[0] * stepY)
            for (i in 1 until heartRateData.size) {
                lineTo(i * stepX, maxY - heartRateData[i] * stepY)
            }
        }

        // Draw the graph line
        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 4f)
        )

        // Draw the data points and labels
        for (i in heartRateData.indices) {
            val x = i * stepX
            val y = maxY - heartRateData[i] * stepY
            drawCircle(Color.Red, radius = 5f, center = androidx.compose.ui.geometry.Offset(x, y))
            drawContext.canvas.nativeCanvas.drawText(
                heartRateData[i].toString(),
                x,
                y - 10,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 24f
                }
            )
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

@Preview(showBackground = true)
@Composable
fun FitnessDashboardPreview() {
    FitnessDashboard()
}


