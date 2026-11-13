package kz.jaryq.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class AppInfo(val name: String, val packageName: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { JaryqLauncherScreen(apps = getInstalledApps()) }
    }

    private fun getInstalledApps(): List<AppInfo> {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        return pm.queryIntentActivities(intent, 0)
            .map { AppInfo(it.loadLabel(pm).toString(), it.activityInfo.packageName) }
            .sortedBy { it.name.lowercase() }
    }
}

@Composable
fun JaryqLauncherScreen(apps: List<AppInfo>) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    val currentDate = remember { SimpleDateFormat("d MMMM, EEEE", Locale("kk", "KZ")).format(Date()) }
    val filteredApps = remember(searchQuery, apps) {
        if (searchQuery.isEmpty()) apps else apps.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0B132B)).padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("ЖАРЫҚ UI", color = Color(0xFFFBBF24), fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Қош келдіңіз!", color = Color(0xFF38BDF8), fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(currentTime, color = Color.White, fontSize = 58.sp, fontWeight = FontWeight.Bold)
        Text(currentDate.replaceFirstChar { it.uppercase() }, color = Color(0xFF94A3B8), fontSize = 18.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { context.startActivity(Intent(Intent.ACTION_DIAL)) },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) { Text("Қоңырау шалу", fontSize = 16.sp, fontWeight = FontWeight.Bold) }

            Button(
                onClick = {
                    try { context.startActivity(Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_MESSAGING) }) } catch (_: Exception) {}
                },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C2541))
            ) { Text("Хабарламалар", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Қосымшаны іздеу...", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFFBBF24), unfocusedBorderColor = Color(0xFF334155)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Барлық қосымшалар", color = Color(0xFF94A3B8), fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredApps) { app ->
                Box(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF1C2541), RoundedCornerShape(12.dp))
                        .clickable { context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it) } }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) { Text(app.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium) }
            }
        }
    }
}
