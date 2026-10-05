package com.hormi.hormiapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hormi.hormiapp.R
import com.hormi.hormiapp.ui.theme.PrimaryGreen

@Composable
fun HormiAppHeader(
    title: String,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .offset(x = (-4).dp), // Desplaza ligeramente todo el bloque a la izquierda
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de atrás (usando Icon + clickable para evitar el padding extra del IconButton)
        if (onBackClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.Black,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onBackClick() }
                    .padding(8.dp) // Área táctil
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        // Logo
        Image(
            painter = painterResource(id = R.drawable.hormiapp_logo),
            contentDescription = "Logo HormiApp",
            modifier = Modifier.size(44.dp)
        )
        
        Spacer(modifier = Modifier.width(10.dp))
        
        // Textos apilados y juntos
        Column {
            Text(
                text = "HormiApp",
                fontSize = 22.sp, // AHORA EL NOMBRE DE LA APP ES MÁS GRANDE
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryGreen
            )
            Text(
                text = title,
                fontSize = 16.sp, // Y EL TÍTULO INFERIOR ES MÁS PEQUEÑO
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}
