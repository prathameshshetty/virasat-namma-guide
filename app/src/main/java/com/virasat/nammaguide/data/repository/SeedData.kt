package com.virasat.nammaguide.data.repository

import com.virasat.nammaguide.data.db.entity.HeritageSite

object SeedData {
    val heritageSites = listOf(
        HeritageSite("hampi_virupaksha", "Virupaksha Temple, Hampi", "ವಿರೂಪಾಕ್ಷ ದೇವಸ್ಥಾನ, ಹಂಪಿ", "Vijayanagara", "Vijayanagara Empire", "7th–15th century CE", "temple", 15.3350, 76.4600, qrCode = "virasat_hampi_virupaksha"),
        HeritageSite("bidar_fort", "Bidar Fort", "ಬಿದರ್ ಕೋಟೆ", "Bidar", "Bahmani Sultanate", "15th century CE", "fort", 17.9144, 77.5200, qrCode = "virasat_bidar_fort"),
        HeritageSite("gol_gumbaz", "Gol Gumbaz, Vijayapura", "ಗೋಲ ಗುಂಬಜ್, ವಿಜಯಪುರ", "Vijayapura", "Adil Shahi Dynasty", "17th century CE", "monument", 16.8311, 75.7234, qrCode = "virasat_gol_gumbaz"),
        HeritageSite("badami_caves", "Badami Cave Temples", "ಬಾದಾಮಿ ಗುಹಾ ದೇವಾಲಯಗಳು", "Bagalkot", "Chalukya Dynasty", "6th–8th century CE", "cave", 15.9200, 75.6762, qrCode = "virasat_badami_caves"),
        HeritageSite("aihole", "Aihole Temple Complex", "ಐಹೊಳೆ ದೇವಾಲಯ ಸಮೂಹ", "Bagalkot", "Chalukya Dynasty", "4th–12th century CE", "temple", 16.0164, 75.8744, qrCode = "virasat_aihole"),
        HeritageSite("pattadakal", "Pattadakal Monuments", "ಪಟ್ಟದಕಲ್ಲು ಸ್ಮಾರಕಗಳು", "Bagalkot", "Chalukya Dynasty", "7th–8th century CE", "temple", 15.9484, 75.8173, qrCode = "virasat_pattadakal"),
        HeritageSite("belur_temple", "Chennakeshava Temple, Belur", "ಚೆನ್ನಕೇಶವ ದೇವಾಲಯ, ಬೇಲೂರು", "Hassan", "Hoysala Empire", "12th century CE", "temple", 13.1651, 75.8700, qrCode = "virasat_belur"),
        HeritageSite("halebidu", "Hoysaleswara Temple, Halebidu", "ಹೊಯ್ಸಳೇಶ್ವರ ದೇವಾಲಯ, ಹಳೇಬೀಡು", "Hassan", "Hoysala Empire", "12th century CE", "temple", 13.2122, 76.0149, qrCode = "virasat_halebidu"),
        HeritageSite("chitradurga_fort", "Chitradurga Fort", "ಚಿತ್ರದುರ್ಗ ಕೋಟೆ", "Chitradurga", "Nayakas of Chitradurga", "15th–18th century CE", "fort", 14.2267, 76.3964, qrCode = "virasat_chitradurga"),
        HeritageSite("gadag_trikuta", "Trikuteshwara Temple, Gadag", "ತ್ರಿಕೂಟೇಶ್ವರ ದೇವಾಲಯ, ಗದಗ", "Gadag", "Western Chalukyas", "11th century CE", "temple", 15.4297, 75.6206, qrCode = "virasat_gadag_trikuta"),
        HeritageSite("lakkundi", "Lakkundi Temple Complex", "ಲಕ್ಕುಂಡಿ ದೇವಾಲಯ ಸಮೂಹ", "Gadag", "Western Chalukyas", "10th–12th century CE", "temple", 15.3963, 75.7092, qrCode = "virasat_lakkundi"),
        HeritageSite("shravanabelagola", "Shravanabelagola Bahubali", "ಶ್ರವಣಬೆಳಗೊಳ ಬಾಹುಬಲಿ", "Hassan", "Ganga Dynasty", "10th century CE", "monument", 12.8587, 76.4876, qrCode = "virasat_shravanabelagola"),
        HeritageSite("srirangapatna", "Srirangapatna Fort", "ಶ್ರೀರಂಗಪಟ್ಟಣ ಕೋಟೆ", "Mandya", "Kingdom of Mysore", "16th–18th century CE", "fort", 12.4244, 76.6936, qrCode = "virasat_srirangapatna"),
        HeritageSite("somanathapura", "Keshava Temple, Somanathapura", "ಕೇಶವ ದೇವಾಲಯ, ಸೋಮನಾಥಪುರ", "Mysuru", "Hoysala Empire", "13th century CE", "temple", 12.2671, 76.8985, qrCode = "virasat_somanathapura"),
        HeritageSite("mysore_palace", "Mysore Palace", "ಮೈಸೂರು ಅರಮನೆ", "Mysuru", "Wadiyar Dynasty", "14th century (rebuilt 1912)", "palace", 12.3052, 76.6552, qrCode = "virasat_mysore_palace"),
        HeritageSite("gulbarga_fort", "Gulbarga Fort", "ಕಲಬುರಗಿ ಕೋಟೆ", "Kalaburagi", "Bahmani Sultanate", "14th century CE", "fort", 17.3291, 76.8228, qrCode = "virasat_gulbarga_fort"),
        HeritageSite("raichur_fort", "Raichur Fort", "ರಾಯಚೂರು ಕೋಟೆ", "Raichur", "Kakatiya Dynasty", "13th century CE", "fort", 16.2050, 77.3566, qrCode = "virasat_raichur_fort"),
        HeritageSite("banavasi_temple", "Madhukeshwara Temple, Banavasi", "ಮಧುಕೇಶ್ವರ ದೇವಾಲಯ, ಬನವಾಸಿ", "Uttara Kannada", "Kadamba Dynasty", "9th century CE", "temple", 14.5330, 75.0300, qrCode = "virasat_banavasi"),
        HeritageSite("melkote_temple", "Cheluvanarayana Temple, Melkote", "ಚೆಲುವನಾರಾಯಣ ದೇವಾಲಯ, ಮೇಲುಕೋಟೆ", "Mandya", "Hoysala Empire", "12th century CE", "temple", 12.6585, 76.6488, qrCode = "virasat_melkote"),
        HeritageSite("talakadu", "Panchalinga Temples, Talakadu", "ಪಂಚಲಿಂಗ ದೇವಾಲಯಗಳು, ತಲಕಾಡು", "Mysuru", "Ganga Dynasty", "9th–11th century CE", "temple", 12.2088, 77.0279, qrCode = "virasat_talakadu")
    )
}
