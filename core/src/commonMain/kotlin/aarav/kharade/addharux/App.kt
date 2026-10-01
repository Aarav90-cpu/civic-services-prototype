package aarav.kharade.addharux

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aarav.kharade.addharux.models.Person
import kotlinx.coroutines.delay

enum class Screen { Dashboard, Identity, Enrollment, Status, Appointments, Profile }
enum class Language { EN, HI, MR }

fun String.localized(lang: Language): String {
    val dict = mapOf(
        "Home" to mapOf(Language.HI to "होम", Language.MR to "मुख्यपृष्ठ"),
        "ID" to mapOf(Language.HI to "पहचान", Language.MR to "ओळख"),
        "Enroll" to mapOf(Language.HI to "पंजीकरण", Language.MR to "नोंदणी"),
        "Status" to mapOf(Language.HI to "स्थिति", Language.MR to "स्थिती"),
        "Visits" to mapOf(Language.HI to "भेंट", Language.MR to "भेटी"),
        "Welcome back," to mapOf(Language.HI to "वापसी पर स्वागत है,", Language.MR to "परत आल्यावर स्वागत आहे,"),
        "Civic Alert" to mapOf(Language.HI to "नागरिक अलर्ट", Language.MR to "नागरी अलर्ट"),
        "Link your mobile number before Jan 31st to ensure uninterrupted services." to mapOf(
            Language.HI to "निर्बाध सेवाओं के लिए 31 जनवरी से पहले अपना मोबाइल नंबर लिंक करें।",
            Language.MR to "अखंडित सेवांसाठी ३१ जानेवारीपूर्वी तुमचा मोबाईल नंबर लिंक करा."
        ),
        "Digital ID" to mapOf(Language.HI to "डिजिटल पहचान", Language.MR to "डिजिटल ओळख"),
        "Manage secure identity" to mapOf(Language.HI to "सुरक्षित पहचान प्रबंधित करें", Language.MR to "सुरक्षित ओळख व्यवस्थापित करा"),
        "Enrollment" to mapOf(Language.HI to "पंजीकरण", Language.MR to "नोंदणी"),
        "Start new workflow" to mapOf(Language.HI to "नया वर्कफ़्लो शुरू करें", Language.MR to "नवीन कार्यप्रवाह सुरू करा"),
        "Track applications" to mapOf(Language.HI to "आवेदन ट्रैक करें", Language.MR to "अर्ज ट्रॅक करा"),
        "Appointments" to mapOf(Language.HI to "नियुक्तियां", Language.MR to "भेटी"),
        "Book physical visits" to mapOf(Language.HI to "भौतिक भेंट बुक करें", Language.MR to "प्रत्यक्ष भेटी बुक करा"),
        "Synthetic Citizen Enrollment" to mapOf(Language.HI to "कृत्रिम नागरिक पंजीकरण", Language.MR to "कृत्रिम नागरिक नोंदणी"),
        "Full Name" to mapOf(Language.HI to "पूरा नाम", Language.MR to "पूर्ण नाव"),
        "Date of Birth (YYYY-MM-DD)" to mapOf(Language.HI to "जन्म तिथि (YYYY-MM-DD)", Language.MR to "जन्मतारीख (YYYY-MM-DD)"),
        "Address" to mapOf(Language.HI to "पता", Language.MR to "पत्ता"),
        "Contact Number" to mapOf(Language.HI to "संपर्क नंबर", Language.MR to "संपर्क क्रमांक"),
        "Submitting..." to mapOf(Language.HI to "जमा किया जा रहा है...", Language.MR to "सबमिट करत आहे..."),
        "Submit Application" to mapOf(Language.HI to "आवेदन जमा करें", Language.MR to "अर्ज सबमिट करा"),
        "Success!" to mapOf(Language.HI to "सफलता!", Language.MR to "यशस्वी!"),
        "Error:" to mapOf(Language.HI to "त्रुटि:", Language.MR to "त्रुटी:"),
        "Unknown error" to mapOf(Language.HI to "अज्ञात त्रुटि", Language.MR to "अज्ञात त्रुटी")
    )
    return dict[this]?.get(lang) ?: this
}

@Composable
// skipLoopback: pass true from physical Android devices so 127.0.0.1 is never tried
fun App(serverHost: String = "127.0.0.1", skipLoopback: Boolean = false, onSubmitSuccess: (() -> Unit)? = null) {
    var isDarkTheme by remember { mutableStateOf(true) }
    var currentLanguage by remember { mutableStateOf(Language.EN) }
    
    val darkColors = darkColorScheme(
        primary = Color(0xFF6366F1),
        secondary = Color(0xFF14B8A6),
        background = Color(0xFF0F172A),
        surface = Color(0xFF1E293B),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFFF8FAFC),
        onSurface = Color(0xFFF8FAFC)
    )
    
    val lightColors = lightColorScheme(
        primary = Color(0xFF4F46E5),
        secondary = Color(0xFF0D9488),
        background = Color(0xFFF8FAFC),
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF0F172A),
        onSurface = Color(0xFF0F172A)
    )

    MaterialTheme(colorScheme = if (isDarkTheme) darkColors else lightColors) {
        var currentScreen by remember { mutableStateOf(Screen.Dashboard) }
        
        Scaffold(
            topBar = { 
                TopSettingsBar(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                    currentLanguage = currentLanguage,
                    onLanguageChange = { currentLanguage = it }
                ) 
            },
            bottomBar = { 
                FloatingBottomBar(currentScreen, currentLanguage) { currentScreen = it } 
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                Crossfade(targetState = currentScreen) { screen ->
                    when (screen) {
                        Screen.Dashboard -> DashboardContent(currentLanguage) { currentScreen = it }
                        Screen.Identity -> PlaceholderScreen("Digital Identity Generation")
                        Screen.Enrollment -> EnrollmentScreen(aarav.kharade.addharux.civic.api.ApiClient(serverHost, skipLoopback), currentLanguage, onSubmitSuccess)
                        Screen.Status -> PlaceholderScreen("Application Status Tracking")
                        Screen.Appointments -> PlaceholderScreen("Appointment Booking")
                        Screen.Profile -> PlaceholderScreen("Profile Detail Updates")
                    }
                }
            }
        }
    }
}

@Composable
fun TopSettingsBar(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    currentLanguage: Language,
    onLanguageChange: (Language) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp)
        ) {
            Language.values().forEach { lang ->
                val isSelected = lang == currentLanguage
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onLanguageChange(lang) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = lang.name,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        IconButton(
            onClick = onThemeToggle,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = "Toggle Theme",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FloatingBottomBar(currentScreen: Screen, currentLanguage: Language, onNavigate: (Screen) -> Unit) {
    val items = listOf(
        Triple(Screen.Dashboard, "Home".localized(currentLanguage), Icons.Rounded.Home),
        Triple(Screen.Identity, "ID".localized(currentLanguage), Icons.Rounded.Badge),
        Triple(Screen.Enrollment, "Enroll".localized(currentLanguage), Icons.Rounded.AppRegistration),
        Triple(Screen.Status, "Status".localized(currentLanguage), Icons.Rounded.Analytics),
        Triple(Screen.Appointments, "Visits".localized(currentLanguage), Icons.Rounded.Event)
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(percent = 50))
                .clip(RoundedCornerShape(percent = 50))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (screen, title, icon) ->
                val isSelected = currentScreen == screen
                val backgroundColor by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                )
                val contentColor by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(backgroundColor)
                        .clickable { onNavigate(screen) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    if (isSelected) {
                        Text(
                            text = title,
                            color = contentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardContent(currentLanguage: Language, onNavigate: (Screen) -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    
    // Fake user data
    val fakeUser = remember {
        Person(
            id = "CX-9021-4822",
            name = "Aarav Sharma",
            dateOfBirth = "1990-05-14",
            address = "42 Silicon Ave, Tech District",
            contact = "+91-9876543210"
        )
    }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { -20 })
        ) {
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Text(
                    text = "Welcome back,".localized(currentLanguage),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = fakeUser.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "ID: ${fakeUser.id}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600)) + slideInHorizontally(initialOffsetX = { 50 })
        ) {
            // News & Alerts Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Civic Alert".localized(currentLanguage),
                        color = Color(0xFF14B8A6),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Link your mobile number before Jan 31st to ensure uninterrupted services.".localized(currentLanguage),
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }
        
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn(tween(500))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(
                        title = "Digital ID".localized(currentLanguage),
                        description = "Manage secure identity".localized(currentLanguage),
                        gradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Identity) }
                    )
                    FeatureCard(
                        title = "Enrollment".localized(currentLanguage),
                        description = "Start new workflow".localized(currentLanguage),
                        gradient = listOf(Color(0xFF14B8A6), Color(0xFF10B981)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Enrollment) }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(
                        title = "Status".localized(currentLanguage),
                        description = "Track applications".localized(currentLanguage),
                        gradient = listOf(Color(0xFFF59E0B), Color(0xFFEF4444)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Status) }
                    )
                    FeatureCard(
                        title = "Appointments".localized(currentLanguage),
                        description = "Book physical visits".localized(currentLanguage),
                        gradient = listOf(Color(0xFFEC4899), Color(0xFFF43F5E)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Appointments) }
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isHovered) 1.02f else 1f)
    val shadow by animateDpAsState(if (isHovered) 16.dp else 8.dp)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .shadow(shadow, RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(gradient))
            .clickable { onClick() }
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Feature under construction",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}
