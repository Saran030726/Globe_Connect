package com.example.ui.util

import com.example.data.model.PlaceEntity
import com.example.data.model.StateEntity
import com.example.data.model.VRExperienceEntity

object NativeImageProvider {

    // High-resolution native photography URLs for major iconic Indian landmarks (Unsplash CDN with optimized quality & size)
    private val FAMOUS_NATIVE_PHOTOS = mapOf(
        // Uttar Pradesh
        "taj mahal" to "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=900&auto=format&fit=crop&q=80",
        "agra fort" to "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=900&auto=format&fit=crop&q=80",
        "fatehpur sikri" to "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=900&auto=format&fit=crop&q=80",
        "kashi vishwanath temple" to "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=900&auto=format&fit=crop&q=80",
        "varanasi" to "https://images.unsplash.com/photo-1571536802807-30451e3955d8?w=900&auto=format&fit=crop&q=80",
        "varanasi ghats" to "https://images.unsplash.com/photo-1571536802807-30451e3955d8?w=900&auto=format&fit=crop&q=80",
        "dashashwamedh ghat" to "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=900&auto=format&fit=crop&q=80",
        "ram mandir" to "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?w=900&auto=format&fit=crop&q=80",
        "sarnath" to "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=900&auto=format&fit=crop&q=80",

        // Tamil Nadu
        "meenakshi amman temple" to "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?w=900&auto=format&fit=crop&q=80",
        "marina beach" to "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=900&auto=format&fit=crop&q=80",
        "shore temple" to "https://images.unsplash.com/photo-1627894483216-2138af692e32?w=900&auto=format&fit=crop&q=80",
        "mahabalipuram" to "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=900&auto=format&fit=crop&q=80",
        "brihadeeswarar temple" to "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?w=900&auto=format&fit=crop&q=80",
        "kodaikanal" to "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=900&auto=format&fit=crop&q=80",
        "kodaikanal lake" to "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=900&auto=format&fit=crop&q=80",
        "ooty" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80",
        "nilgiri mountain railway" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80",
        "kanyakumari" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",
        "vivekananda rock memorial" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",
        "kolli hills" to "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=900&auto=format&fit=crop&q=80",
        "yelagiri" to "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=900&auto=format&fit=crop&q=80",
        "yercaud" to "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=900&auto=format&fit=crop&q=80",

        // Punjab
        "golden temple" to "https://images.unsplash.com/photo-1597042045610-8b1b223c21c7?w=900&auto=format&fit=crop&q=80",
        "amritsar" to "https://images.unsplash.com/photo-1588096344356-9b50e4176669?w=900&auto=format&fit=crop&q=80",
        "wagah border" to "https://images.unsplash.com/photo-1588096344356-9b50e4176669?w=900&auto=format&fit=crop&q=80",
        "jallianwala bagh" to "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=900&auto=format&fit=crop&q=80",

        // Maharashtra
        "gateway of india" to "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?w=900&auto=format&fit=crop&q=80",
        "marine drive" to "https://images.unsplash.com/photo-1567157577867-05ccb1388e66?w=900&auto=format&fit=crop&q=80",
        "elephanta caves" to "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=900&auto=format&fit=crop&q=80",
        "ajanta caves" to "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=900&auto=format&fit=crop&q=80",
        "ellora caves" to "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=900&auto=format&fit=crop&q=80",
        "lonavala" to "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=900&auto=format&fit=crop&q=80",
        "mahabaleshwar" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80",

        // Rajasthan
        "hawa mahal" to "https://images.unsplash.com/photo-1599661046289-e31897846e41?w=900&auto=format&fit=crop&q=80",
        "amer fort" to "https://images.unsplash.com/photo-1609137144820-210e309cb9f6?w=900&auto=format&fit=crop&q=80",
        "city palace udaipur" to "https://images.unsplash.com/photo-1615836245337-f5b9b2303f10?w=900&auto=format&fit=crop&q=80",
        "lake pichola" to "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=900&auto=format&fit=crop&q=80",
        "mehrangarh fort" to "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?w=900&auto=format&fit=crop&q=80",
        "jaisalmer fort" to "https://images.unsplash.com/photo-1609137144820-210e309cb9f6?w=900&auto=format&fit=crop&q=80",
        "sam sand dunes" to "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=900&auto=format&fit=crop&q=80",
        "pushkar lake" to "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=900&auto=format&fit=crop&q=80",

        // Telangana & Andhra Pradesh
        "charminar" to "https://images.unsplash.com/photo-1616423640778-28d1b53229bd?w=900&auto=format&fit=crop&q=80",
        "golconda fort" to "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=900&auto=format&fit=crop&q=80",
        "tirumala venkateswara temple" to "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?w=900&auto=format&fit=crop&q=80",
        "tirupati" to "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?w=900&auto=format&fit=crop&q=80",
        "araku valley" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80",
        "borra caves" to "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=900&auto=format&fit=crop&q=80",
        "gandikota" to "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=900&auto=format&fit=crop&q=80",
        "gandikota canyon" to "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=900&auto=format&fit=crop&q=80",

        // West Bengal
        "victoria memorial" to "https://images.unsplash.com/photo-1558431382-27e303142255?w=900&auto=format&fit=crop&q=80",
        "howrah bridge" to "https://images.unsplash.com/photo-1608958435020-e8a7109ba809?w=900&auto=format&fit=crop&q=80",
        "darjeeling" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80",
        "tiger hill" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",
        "sunderbans" to "https://images.unsplash.com/photo-1534177616072-ef7dc120449d?w=900&auto=format&fit=crop&q=80",

        // Jammu & Kashmir
        "dal lake" to "https://images.unsplash.com/photo-1598091383021-15ddea10925d?w=900&auto=format&fit=crop&q=80",
        "gulmarg" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",
        "gulmarg gondola" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",
        "pahalgam" to "https://images.unsplash.com/photo-1566837945700-30057527ade0?w=900&auto=format&fit=crop&q=80",
        "betaab valley" to "https://images.unsplash.com/photo-1566837945700-30057527ade0?w=900&auto=format&fit=crop&q=80",
        "sonamarg" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",

        // Kerala
        "munnar" to "https://images.unsplash.com/photo-1593693397690-362cb9666fc2?w=900&auto=format&fit=crop&q=80",
        "alappuzha" to "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=900&auto=format&fit=crop&q=80",
        "alappuzha backwaters" to "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=900&auto=format&fit=crop&q=80",
        "athirappilly waterfalls" to "https://images.unsplash.com/photo-1546587348-d12660c30c50?w=900&auto=format&fit=crop&q=80",
        "varkala beach" to "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=900&auto=format&fit=crop&q=80",
        "kovalam beach" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",
        "wayanad" to "https://images.unsplash.com/photo-1511497584788-87676104235f?w=900&auto=format&fit=crop&q=80",

        // Karnataka
        "mysore palace" to "https://images.unsplash.com/photo-1600100397608-f010f4439ff0?w=900&auto=format&fit=crop&q=80",
        "jog falls" to "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=900&auto=format&fit=crop&q=80",
        "coorg" to "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=900&auto=format&fit=crop&q=80",
        "gokarna" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",
        "hampi" to "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=900&auto=format&fit=crop&q=80",

        // Goa
        "basilica of bom jesus" to "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=900&auto=format&fit=crop&q=80",
        "dudhsagar falls" to "https://images.unsplash.com/photo-1546587348-d12660c30c50?w=900&auto=format&fit=crop&q=80",
        "baga beach" to "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=900&auto=format&fit=crop&q=80",
        "calangute beach" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",
        "palolem beach" to "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=900&auto=format&fit=crop&q=80",
        "fort aguada" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",

        // Gujarat
        "white rann of kutch" to "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=900&auto=format&fit=crop&q=80",
        "somnath temple" to "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=900&auto=format&fit=crop&q=80",
        "dwarka" to "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=900&auto=format&fit=crop&q=80",
        "gir national park" to "https://images.unsplash.com/photo-1534177616072-ef7dc120449d?w=900&auto=format&fit=crop&q=80",
        "rani ki vav" to "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=900&auto=format&fit=crop&q=80",
        "statue of unity" to "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=900&auto=format&fit=crop&q=80",

        // Madhya Pradesh
        "sanchi stupa" to "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=900&auto=format&fit=crop&q=80",
        "khajuraho" to "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=900&auto=format&fit=crop&q=80",
        "bhedaghat" to "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=900&auto=format&fit=crop&q=80",
        "kanha national park" to "https://images.unsplash.com/photo-1534177616072-ef7dc120449d?w=900&auto=format&fit=crop&q=80",

        // Chhattisgarh
        "chitrakote waterfalls" to "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=900&auto=format&fit=crop&q=80",
        "tirathgarh falls" to "https://images.unsplash.com/photo-1546587348-d12660c30c50?w=900&auto=format&fit=crop&q=80",

        // Assam & North-East
        "kaziranga national park" to "https://images.unsplash.com/photo-1534177616072-ef7dc120449d?w=900&auto=format&fit=crop&q=80",
        "kamakhya temple" to "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?w=900&auto=format&fit=crop&q=80",
        "majuli" to "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=900&auto=format&fit=crop&q=80",
        "tawang monastery" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80",
        "sela pass" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",
        "double decker living root bridge" to "https://images.unsplash.com/photo-1511497584788-87676104235f?w=900&auto=format&fit=crop&q=80",
        "cherrapunji" to "https://images.unsplash.com/photo-1511497584788-87676104235f?w=900&auto=format&fit=crop&q=80",
        "dawki" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",
        "loktak lake" to "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=900&auto=format&fit=crop&q=80",
        "dzukou valley" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80",
        "gurudongmar lake" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",

        // Odisha
        "konark sun temple" to "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=900&auto=format&fit=crop&q=80",
        "jagannath temple puri" to "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=900&auto=format&fit=crop&q=80",
        "chilika lake" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",
        "puri beach" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",

        // Bihar
        "mahabodhi temple" to "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=900&auto=format&fit=crop&q=80",
        "nalanda university ruins" to "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=900&auto=format&fit=crop&q=80",

        // Himachal Pradesh
        "shimla" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",
        "manali" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",
        "rohtang pass" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",
        "solang valley" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",
        "spiti valley" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",

        // Uttarakhand
        "kedarnath" to "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?w=900&auto=format&fit=crop&q=80",
        "kedarnath temple" to "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?w=900&auto=format&fit=crop&q=80",
        "badrinath" to "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?w=900&auto=format&fit=crop&q=80",
        "badrinath temple" to "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?w=900&auto=format&fit=crop&q=80",
        "rishikesh" to "https://images.unsplash.com/photo-1571536802807-30451e3955d8?w=900&auto=format&fit=crop&q=80",
        "haridwar" to "https://images.unsplash.com/photo-1571536802807-30451e3955d8?w=900&auto=format&fit=crop&q=80",
        "har ki pauri" to "https://images.unsplash.com/photo-1571536802807-30451e3955d8?w=900&auto=format&fit=crop&q=80",
        "nainital" to "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=900&auto=format&fit=crop&q=80",
        "mussoorie" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80"
    )

    // Curated native signature state hero wallpapers
    private val STATE_HERO_PHOTOS = mapOf(
        "Andhra Pradesh" to "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=900&auto=format&fit=crop&q=80", // Gandikota
        "Arunachal Pradesh" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80", // Tawang
        "Assam" to "https://images.unsplash.com/photo-1534177616072-ef7dc120449d?w=900&auto=format&fit=crop&q=80", // Kaziranga
        "Bihar" to "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=900&auto=format&fit=crop&q=80", // Bodh Gaya
        "Chhattisgarh" to "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=900&auto=format&fit=crop&q=80", // Chitrakote
        "Goa" to "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=900&auto=format&fit=crop&q=80", // Palolem & Church
        "Gujarat" to "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=900&auto=format&fit=crop&q=80", // Rann of Kutch
        "Haryana" to "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=900&auto=format&fit=crop&q=80", // Kurukshetra
        "Himachal Pradesh" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80", // Shimla / Solang
        "Jharkhand" to "https://images.unsplash.com/photo-1546587348-d12660c30c50?w=900&auto=format&fit=crop&q=80", // Hundru Falls
        "Karnataka" to "https://images.unsplash.com/photo-1600100397608-f010f4439ff0?w=900&auto=format&fit=crop&q=80", // Mysore Palace
        "Kerala" to "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=900&auto=format&fit=crop&q=80", // Alleppey Backwaters
        "Madhya Pradesh" to "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=900&auto=format&fit=crop&q=80", // Khajuraho
        "Maharashtra" to "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?w=900&auto=format&fit=crop&q=80", // Gateway of India
        "Manipur" to "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=900&auto=format&fit=crop&q=80", // Loktak Lake
        "Meghalaya" to "https://images.unsplash.com/photo-1511497584788-87676104235f?w=900&auto=format&fit=crop&q=80", // Living Root Bridge
        "Mizoram" to "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80", // Lushai Hills
        "Nagaland" to "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=900&auto=format&fit=crop&q=80", // Dzukou Valley
        "Odisha" to "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=900&auto=format&fit=crop&q=80", // Konark Sun Temple
        "Punjab" to "https://images.unsplash.com/photo-1597042045610-8b1b223c21c7?w=900&auto=format&fit=crop&q=80", // Golden Temple
        "Rajasthan" to "https://images.unsplash.com/photo-1599661046289-e31897846e41?w=900&auto=format&fit=crop&q=80", // Hawa Mahal
        "Sikkim" to "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80", // Kanchenjunga
        "Tamil Nadu" to "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?w=900&auto=format&fit=crop&q=80", // Meenakshi Temple
        "Telangana" to "https://images.unsplash.com/photo-1616423640778-28d1b53229bd?w=900&auto=format&fit=crop&q=80", // Charminar
        "Tripura" to "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=900&auto=format&fit=crop&q=80", // Neermahal
        "Uttar Pradesh" to "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=900&auto=format&fit=crop&q=80", // Taj Mahal
        "Uttarakhand" to "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?w=900&auto=format&fit=crop&q=80", // Kedarnath
        "West Bengal" to "https://images.unsplash.com/photo-1558431382-27e303142255?w=900&auto=format&fit=crop&q=80", // Victoria Memorial
        "Jammu & Kashmir (UT)" to "https://images.unsplash.com/photo-1598091383021-15ddea10925d?w=900&auto=format&fit=crop&q=80" // Dal Lake
    )

    // Curated native photo collections per category for authentic regional depth
    private val CATEGORY_NATIVE_FALLBACKS = mapOf(
        "Pilgrimage & Heritage" to listOf(
            "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1597042045610-8b1b223c21c7?w=900&auto=format&fit=crop&q=80"
        ),
        "History & Heritage" to listOf(
            "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1600100397608-f010f4439ff0?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1615836245337-f5b9b2303f10?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1609137144820-210e309cb9f6?w=900&auto=format&fit=crop&q=80"
        ),
        "Waterfalls" to listOf(
            "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1546587348-d12660c30c50?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=900&auto=format&fit=crop&q=80"
        ),
        "Beaches & Coastal" to listOf(
            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=900&auto=format&fit=crop&q=80"
        ),
        "Lakes & Water Bodies" to listOf(
            "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1598091383021-15ddea10925d?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=900&auto=format&fit=crop&q=80"
        ),
        "Wildlife & Nature" to listOf(
            "https://images.unsplash.com/photo-1534177616072-ef7dc120449d?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1511497584788-87676104235f?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=900&auto=format&fit=crop&q=80"
        ),
        "Caves & Geotourism" to listOf(
            "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=900&auto=format&fit=crop&q=80"
        ),
        "Mountains & Valleys" to listOf(
            "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1548013146-72479768bada?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1566837945700-30057527ade0?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=900&auto=format&fit=crop&q=80"
        ),
        "Trekking & Eco-Trails" to listOf(
            "https://images.unsplash.com/photo-1511497584788-87676104235f?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=900&auto=format&fit=crop&q=80"
        ),
        "Culture & Rural" to listOf(
            "https://images.unsplash.com/photo-1593693397690-362cb9666fc2?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=900&auto=format&fit=crop&q=80"
        ),
        "Hidden Discovery" to listOf(
            "https://images.unsplash.com/photo-1511497584788-87676104235f?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=900&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1546587348-d12660c30c50?w=900&auto=format&fit=crop&q=80"
        )
    )

    fun getStateHeroUrl(stateName: String): String {
        return STATE_HERO_PHOTOS[stateName.trim()]
            ?: "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=900&auto=format&fit=crop&q=80"
    }

    fun getNativeImageUrl(place: PlaceEntity): String {
        val nameLower = place.name.trim().lowercase()

        // 1. Exact match with famous landmarks
        if (FAMOUS_NATIVE_PHOTOS.containsKey(nameLower)) {
            return FAMOUS_NATIVE_PHOTOS[nameLower]!!
        }

        // 2. Keyword check in landmark table
        for ((landmarkKey, photoUrl) in FAMOUS_NATIVE_PHOTOS) {
            if (nameLower.contains(landmarkKey) || landmarkKey.contains(nameLower)) {
                return photoUrl
            }
        }

        // 3. Match by place keywords in name
        if (nameLower.contains("waterfall") || nameLower.contains("falls") || nameLower.contains("jharna")) {
            val list = CATEGORY_NATIVE_FALLBACKS["Waterfalls"]!!
            return list[kotlin.math.abs(place.id) % list.size]
        }
        if (nameLower.contains("beach") || nameLower.contains("sea") || nameLower.contains("coast")) {
            val list = CATEGORY_NATIVE_FALLBACKS["Beaches & Coastal"]!!
            return list[kotlin.math.abs(place.id) % list.size]
        }
        if (nameLower.contains("temple") || nameLower.contains("mandir") || nameLower.contains("dham") || nameLower.contains("church") || nameLower.contains("mosque")) {
            val list = CATEGORY_NATIVE_FALLBACKS["Pilgrimage & Heritage"]!!
            return list[kotlin.math.abs(place.id) % list.size]
        }
        if (nameLower.contains("fort") || nameLower.contains("palace") || nameLower.contains("qila") || nameLower.contains("mahal") || nameLower.contains("museum")) {
            val list = CATEGORY_NATIVE_FALLBACKS["History & Heritage"]!!
            return list[kotlin.math.abs(place.id) % list.size]
        }
        if (nameLower.contains("lake") || nameLower.contains("dam") || nameLower.contains("tal") || nameLower.contains("river") || nameLower.contains("ghat")) {
            val list = CATEGORY_NATIVE_FALLBACKS["Lakes & Water Bodies"]!!
            return list[kotlin.math.abs(place.id) % list.size]
        }
        if (nameLower.contains("cave") || nameLower.contains("caves") || nameLower.contains("gufa")) {
            val list = CATEGORY_NATIVE_FALLBACKS["Caves & Geotourism"]!!
            return list[kotlin.math.abs(place.id) % list.size]
        }
        if (nameLower.contains("peak") || nameLower.contains("hill") || nameLower.contains("valley") || nameLower.contains("pass") || nameLower.contains("ghati")) {
            val list = CATEGORY_NATIVE_FALLBACKS["Mountains & Valleys"]!!
            return list[kotlin.math.abs(place.id) % list.size]
        }

        // 4. Deterministic State + Category hash fallback ensuring visual variety
        val categoryList = CATEGORY_NATIVE_FALLBACKS[place.category]
            ?: CATEGORY_NATIVE_FALLBACKS["History & Heritage"]!!
        val index = kotlin.math.abs(place.id) % categoryList.size
        return categoryList[index]
    }

    fun getVRPreviewUrl(vrExperience: VRExperienceEntity): String {
        return getVRPanoramaUrl(vrExperience.spotName)
    }

    fun getVRPanoramaUrl(spotName: String, category: String = ""): String {
        val spotLower = spotName.trim().lowercase()

        // 1. Direct landmark match
        for ((key, url) in FAMOUS_NATIVE_PHOTOS) {
            if (spotLower.contains(key) || key.contains(spotLower)) {
                return url.replace("w=900", "w=1600")
            }
        }

        // 2. Keyword fallback for 360 panoramic vistas
        if (spotLower.contains("temple") || spotLower.contains("mandir") || spotLower.contains("ghat")) {
            return "https://images.unsplash.com/photo-1571536802807-30451e3955d8?w=1600&auto=format&fit=crop&q=85"
        }
        if (spotLower.contains("fort") || spotLower.contains("palace") || spotLower.contains("mahal")) {
            return "https://images.unsplash.com/photo-1599661046289-e31897846e41?w=1600&auto=format&fit=crop&q=85"
        }
        if (spotLower.contains("lake") || spotLower.contains("beach") || spotLower.contains("sea") || spotLower.contains("water")) {
            return "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?w=1600&auto=format&fit=crop&q=85"
        }
        if (spotLower.contains("mountain") || spotLower.contains("hill") || spotLower.contains("valley") || spotLower.contains("pass")) {
            return "https://images.unsplash.com/photo-1548013146-72479768bada?w=1600&auto=format&fit=crop&q=85"
        }

        return "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=1600&auto=format&fit=crop&q=85"
    }

    fun getCuratedPhotoGallery(place: PlaceEntity): List<String> {
        val primary = getNativeImageUrl(place)
        val stateHero = getStateHeroUrl(place.stateName)
        val categoryList = CATEGORY_NATIVE_FALLBACKS[place.category]
            ?: CATEGORY_NATIVE_FALLBACKS["Mountains & Valleys"]!!
        val secondary = categoryList[(kotlin.math.abs(place.id) + 1) % categoryList.size]
        val tertiary = categoryList[(kotlin.math.abs(place.id) + 2) % categoryList.size]
        return listOf(primary, secondary, stateHero, tertiary).distinct()
    }

    fun getWishlistImageUrl(wish: com.example.data.model.WishlistEntity): String {
        return getNativeImageUrl(
            PlaceEntity(
                id = wish.placeId,
                stateId = 0,
                stateName = wish.stateName,
                name = wish.placeName,
                slug = "",
                category = wish.category,
                isHidden = wish.isHidden,
                codeInPdf = "",
                source = ""
            )
        )
    }
}
