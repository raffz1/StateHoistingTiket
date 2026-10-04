package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

// Enum untuk merepresentasikan kondisi status
enum class StatusPesanan {
    AWAL,
    KOSONG,
    MEMPROSES,
    BERHASIL
}

// ==========================================
// 1. PARENT COMPOSABLE (Stateful Container)
// ==========================================
@Composable
fun TiketScreen() {
    // 3 State yang wajib dikelola oleh Parent (State Hoisting)
    val hargaTiket = 50000
    var jumlahTiket by remember { mutableIntStateOf(1) }
    var namaPembeli by remember { mutableStateOf("") }

    // State untuk kontrol status dan trigger coroutine LaunchedEffect
    var status by remember { mutableStateOf(StatusPesanan.AWAL) }
    var triggerProses by remember { mutableStateOf(0) }

    // Side-Effect coroutine menggunakan LaunchedEffect
    LaunchedEffect(triggerProses) {
        if (triggerProses > 0) {
            status = StatusPesanan.MEMPROSES
            // Delay simulasi proses pemesanan (misal 5 detik sesuai instruksi prompt)
            delay(5000L)
            status = StatusPesanan.BERHASIL
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // App Bar Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1976D2))
                .padding(vertical = 18.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "Pemesanan Tiket",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Child Stateless Form
        FormTiketStateless(
            namaPembeli = namaPembeli,
            jumlahTiket = jumlahTiket,
            hargaTiket = hargaTiket,
            status = status,
            onNamaChange = { namaPembeli = it },
            onJumlahChange = { jumlahTiket = it },
            onPesanClick = {
                if (namaPembeli.trim().isEmpty()) {
                    status = StatusPesanan.KOSONG
                } else {
                    triggerProses++
                }
            }
        )
    }
}

// ==========================================
// 2. CHILD COMPOSABLE (Stateless Component)
// ==========================================
@Composable
fun FormTiketStateless(
    namaPembeli: String,
    jumlahTiket: Int,
    hargaTiket: Int,
    status: StatusPesanan,
    onNamaChange: (String) -> Unit,
    onJumlahChange: (Int) -> Unit,
    onPesanClick: () -> Unit
) {
    val totalBayar = jumlahTiket * hargaTiket
    val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Info Harga
        Text(
            text = "Harga per Tiket: Rp${formatRupiah.format(hargaTiket)} (Total: Rp${formatRupiah.format(totalBayar)})",
            fontSize = 13.sp,
            color = Color(0xFF555555),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Nama
        Text(text = "Nama", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = namaPembeli,
            onValueChange = onNamaChange,
            placeholder = { Text("Masukkan nama Anda") },
            modifier = Modifier.fillMaxWidth(),
            enabled = status != StatusPesanan.MEMPROSES,
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Jumlah Tiket
        Text(text = "Jumlah Tiket", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (jumlahTiket > 1) onJumlahChange(jumlahTiket - 1) },
                enabled = status != StatusPesanan.MEMPROSES && jumlahTiket > 1,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "$jumlahTiket",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.5f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(
                onClick = { onJumlahChange(jumlahTiket + 1) },
                enabled = status != StatusPesanan.MEMPROSES,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Pesan Tiket
        Button(
            onClick = onPesanClick,
            enabled = status != StatusPesanan.MEMPROSES,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2),
                disabledContainerColor = Color(0xFF90CAF9)
            )
        ) {
            Text(
                text = "Pesan Tiket",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Banner Status Dinamis
        StatusBanner(status = status)
    }
}

// ==========================================
// 3. KOMPONEN BANNER STATUS
// ==========================================
@Composable
fun StatusBanner(status: StatusPesanan) {
    val (bgColor, contentColor, iconColor) = when (status) {
        StatusPesanan.AWAL -> Triple(Color(0xFFF5F5F5), Color(0xFF616161), Color.Gray)
        StatusPesanan.KOSONG -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), Color(0xFFD32F2F))
        StatusPesanan.MEMPROSES -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), Color(0xFF1976D2))
        StatusPesanan.BERHASIL -> Triple(Color(0xFFE8F5E9), Color(0xFF388E3C), Color(0xFF388E3C))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when (status) {
                StatusPesanan.AWAL -> {
                    Text(
                        text = "Status: Silakan pesan tiket",
                        color = contentColor,
                        fontSize = 13.sp
                    )
                }
                StatusPesanan.KOSONG -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Status: Nama masih kosong",
                        color = contentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                StatusPesanan.MEMPROSES -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = iconColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Status: Memproses pesanan...",
                        color = contentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                StatusPesanan.BERHASIL -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Status: Tiket telah dipesan",
                        color = contentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}