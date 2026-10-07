package com.hormi.hormiapp.ui.creditos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hormi.hormiapp.R
import com.hormi.hormiapp.ui.components.HormiAppHeader
import com.hormi.hormiapp.ui.theme.AccentYellow
import com.hormi.hormiapp.ui.theme.PrimaryGreen

@Composable
fun CreditosScreen(
    onBackClick: () -> Unit,
    onLogoClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFF9F6F0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        HormiAppHeader(
            title = "Créditos",
            onBackClick = onBackClick,
            onLogoClick = onLogoClick,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Logo
            Image(
                painter = painterResource(id = R.drawable.hormiapp_logo),
                contentDescription = "Logo HormiApp",
                modifier = Modifier.size(96.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // App Name
            Text(
                text = "HormiApp",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Subtitle
            Text(
                text = "Versión 1.0 · Aplicaciones Móviles",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Cards
            TeamMemberCard(
                initials = "SC",
                initialsColor = PrimaryGreen,
                name = "Sebastián Cruz",
                roleIcon = Icons.Default.Palette,
                roleText = "Diseño: boceto y wireframe en Figma"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TeamMemberCard(
                initials = "SM",
                initialsColor = Color(0xFFE69A3B),
                name = "Sebastián Muñoz",
                roleIcon = Icons.Default.Code,
                roleText = "Desarrollo Android"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TeamMemberCard(
                initials = "JL",
                initialsColor = Color(0xFF8D4F35),
                name = "Juan David Londoño",
                roleIcon = Icons.Default.Code,
                roleText = "Desarrollo Android"
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Hecho en Medellín, Colombia", color = Color.Gray, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Funciona sin internet", color = Color.Gray, fontSize = 13.sp)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TeamMemberCard(
    initials: String,
    initialsColor: Color,
    name: String,
    roleIcon: ImageVector,
    roleText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(initialsColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Texts
            Column {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = roleIcon,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = roleText,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
