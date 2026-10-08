#!/usr/bin/env python3
import json
import re

# Master definition of the 29 sections and their exact entries from the PDF OCR
RAW_DATA = {}

RAW_DATA["Andhra Pradesh"] = {
  "code": "AP",
  "capital": "Amaravati",
  "best_season": "October to March",
  "description": "The Sunrise State featuring sacred shrines, scenic Coringa mangroves, Gandikota canyon, and Araku hill ranges.",
  "major": [
    "Tirumala Venkateswara Temple", "Tirupati", "Srikalahasti Temple", "Kanipakam Vinayaka Temple",
    "Chandragiri Fort", "Talakona Waterfalls", "Sri Venkateswara National Park", "Horsley Hills",
    "Kailasakona Waterfalls", "Lepakshi Veerabhadra Temple", "Penukonda Fort", "Puttaparthi",
    "Gandikota Fort", "Gandikota Canyon", "Belum Caves", "Yaganti Temple", "Ahobilam",
    "Mahanandi Temple", "Srisailam", "Srisailam Dam", "Mallikarjuna Temple", "Nagarjuna Sagar",
    "Amaravati Stupa", "Amaralingeswara Temple", "Undavalli Caves", "Kondaveedu Fort",
    "Mangalagiri", "Uppalapadu Bird Sanctuary", "Vijayawada Kanaka Durga Temple", "Prakasam Barrage",
    "Bhavani Island", "Mogalarajapuram Caves", "Kolleru Lake", "Eluru", "Konaseema",
    "Antarvedi", "Antarvedi Beach", "Coringa Wildlife Sanctuary", "Kakinada Beach", "Hope Island",
    "Draksharamam Temple", "Annavaram Temple", "Samalkota Kumararama Temple", "Rajahmundry",
    "Dowleswaram Barrage", "Papikondalu", "Godavari River", "Pochavaram", "Araku Valley",
    "Borra Caves", "Katiki Waterfalls", "Chaparai Waterfalls", "Ananthagiri Hills", "Lambasingi",
    "Vanjangi", "Galikonda View Point", "Tribal Museum Araku", "Padmapuram Gardens",
    "Simhachalam Temple", "Visakhapatnam", "RK Beach", "Kailasagiri", "Rushikonda Beach",
    "Yarada Beach", "Dolphin's Nose", "Submarine Museum", "Bheemili Beach", "Thotlakonda",
    "Bavikonda", "Kondakarla Ava", "Erra Matti Dibbalu", "Arasavalli Sun Temple",
    "Srikurmam Temple", "Baruva Beach", "Kalingapatnam Beach", "Salihundam", "Vizianagaram Fort",
    "Bobbili Fort", "Ramateertham", "Mypadu Beach", "Pulicat Lake", "Nelapattu Bird Sanctuary",
    "Udayagiri Fort", "Venkatagiri Fort", "Penchalakona", "Somashila Reservoir",
    "Pulicat Bird Sanctuary", "Rollapadu Wildlife Sanctuary", "Oravakallu Rock Garden",
    "Kurnool Fort", "Kondareddy Buruju", "Adoni Fort", "Owk Reservoir", "Mylavaram Dam",
    "Dharmavaram", "Kadiri Lakshmi Narasimha Temple", "Rayachoti Veerabhadra Temple",
    "Horsley Hills Viewpoint", "Ranganatha Swamy Temple Nellore", "Vodarevu Beach"
  ],
  "hidden": [
    "Vanjangi Sunrise Point", "Gunjigundam Waterfalls", "Tatapudi Reservoir", "Paderu Coffee Plantations",
    "Minumuluru", "Araku Tribal Villages", "Devarapalli Hills", "Ananthagiri Coffee Trails",
    "Borra Village", "Galikonda Peak Trails", "Matsyagundam", "Gudem Kotha Veedhi", "Sileru",
    "Machkund Waterfalls", "Duduma Waterfalls", "Lambasingi Strawberry Farms", "Kothapalli Waterfalls",
    "Thatikonda", "Kondapalli Forest Trails", "Amaravathi Dhyana Buddha Park", "Uppalapadu Village Trails",
    "Undavalli Riverside Cliffs", "Nagireddypalli Caves", "Ethipothala Nearby Trails", "Mallela Theertham",
    "Akkamahadevi Caves", "Nallamala Forest Viewpoints", "Diguvametta", "Rollapadu Birding Trails",
    "Owk Cave Areas", "Yerraguntla Rock Formations", "Gandikota Villages", "Belum Village Countryside",
    "Banaganapalle Rock Sites", "Nandyal Forest Trails", "Aroor Village", "Kondaveedu Rural Trails",
    "Durgi Handicraft Village", "Mangalagiri Handloom Area", "Bapatla Backwaters", "Dindi Backwaters",
    "Kolleru Fishing Villages", "Coringa Mangrove Trails", "Tallarevu Mangroves", "Konaseema Coconut Villages",
    "Dindi Coconut Lagoons", "Antarvedi Riverside Village", "Uppada Weaving Village",
    "Kalingapatnam River Mouth", "Srikakulam Rural Temple Trail"
  ]
}

RAW_DATA["Arunachal Pradesh"] = {
  "code": "AR",
  "capital": "Itanagar",
  "best_season": "October to April",
  "description": "Land of Dawn-lit Mountains, ancient monasteries, high passes, pristine valleys, and vibrant indigenous tribes.",
  "major": [
    "Tawang Monastery", "Tawang War Memorial", "Sela Pass", "Sela Lake", "Bum La Pass",
    "Madhuri Lake", "Nuranang Falls", "Jaswant Garh", "P.T. Tso Lake", "Shonga-Tser Lake",
    "Tawang Chu Valley", "Urgelling Monastery", "Gorsam Chorten", "Zemithang", "Mukto",
    "Pangang Teng Tso", "Tawang Viewpoint", "Dirang", "Dirang Dzong", "Sangti Valley",
    "Sangti Lake", "Mandala Top", "Kalachakra Gompa", "Bomdila Monastery", "Bomdila Viewpoint",
    "Eaglenest Wildlife Sanctuary", "Rupa", "Shergaon", "Chilipam Monastery", "Tipi Orchidarium",
    "Bhalukpong", "Pakke Tiger Reserve", "Pakhui Wildlife Sanctuary", "Seijosa", "Eaglenest Pass",
    "Itanagar", "Ita Fort", "Ganga Lake", "Jawaharlal Nehru State Museum", "Indira Gandhi Park",
    "Gompa Itanagar", "Naharlagun", "Ziro Valley", "Talley Valley Wildlife Sanctuary", "Ziro Puto",
    "Meghna Cave Temple", "Hong Village", "Hapoli", "Dolo Mando", "Pine Grove Ziro",
    "Daporijo", "Along", "Mechuka", "Mechuka Monastery", "Samtenyangchag Monastery",
    "Dorje Dhargey Waterfall", "Siyom River", "Yingkiong", "Siang River", "Pasighat",
    "Daying Ering Wildlife Sanctuary", "Kekar Monying", "Pongging Viewpoint", "Jengging",
    "Tuting", "Gelling", "Mouling National Park", "Roing", "Mehao Wildlife Sanctuary",
    "Mehao Lake", "Mayudia Pass", "Sally Lake", "Bhismaknagar Fort", "Hunli",
    "Dong Valley", "Walong", "Kibithu", "Kaho Village", "Parasuram Kund", "Namsai",
    "Golden Pagoda", "Chongkham", "Namdapha National Park", "Miao", "Miao Mini Zoo",
    "Jairampur", "Stilwell Road", "Changlang", "Kharsang", "Tezu", "Glow Lake",
    "Hayuliang", "Hawai", "Anini", "Dree Valley", "Dibang Valley", "Etalin",
    "Mishmi Hills", "Mehao Viewpoint", "Kamlang Wildlife Sanctuary"
  ],
  "hidden": [
    "Anini Village Trails", "Etalin Valley", "Anjaw Village Circuit", "Kaho Riverside",
    "Kibithu Riverside", "Dong Sunrise Point", "Walong War Sites", "Hawai Forest Trails",
    "Hayuliang Valley", "Glow Lake Trail", "Lohit Valley", "Wakro", "Parasuram Kund Forest Trail",
    "Tezu Riverfront", "Golden Pagoda Countryside", "Chongkham Village", "Namsai Forest Trails",
    "Miao Eco Trails", "Jairampur Heritage Trail", "Namchick Hathi Phum", "Kharsang Forest Area",
    "Changlang Tea Gardens", "Tirap Tribal Villages", "Longding Hills", "Pongchau Valley",
    "Kanubari", "Deomali", "Seppa", "Pake Kessang", "Pakke Forest Villages", "Nyishi Village Trails",
    "Sangti Rural Valley", "Mandala Hiking Trails", "Dirang Hot Springs", "Thembang Fortified Village",
    "Lhou Village", "Shergaon Apple Orchards", "Chug Valley", "Rupa Forest Trails",
    "Talle Valley Villages", "Hong Village Rice Terraces", "Daporijo Tribal Markets", "Taliha",
    "Siyum Valley", "Geku", "Siang Foothill Villages", "Jengging Forest Trails",
    "Tuting Tribal Villages", "Gelling Border Valley", "Mouling Foothill Villages"
  ]
}

RAW_DATA["Assam"] = {
  "code": "AS",
  "capital": "Dispur",
  "best_season": "November to April",
  "description": "Gateway to Northeast India, famous for one-horned rhinos, tea estates, Kamakhya shrine, and the Brahmaputra River.",
  "major": [
    "Guwahati", "Kamakhya Temple", "Umananda Temple", "Assam State Museum", "Assam State Zoo",
    "Deepor Beel", "Navagraha Temple", "Basistha Ashram", "Srimanta Sankardev Kalakshetra",
    "Pobitora Wildlife Sanctuary", "Hajo", "Hayagriva Madhava Temple", "Sualkuchi",
    "Madan Kamdev", "Chandubi Lake", "Sonapur", "Orang National Park", "Kaziranga National Park",
    "Kohora", "Bagori", "Agoratoli", "Kaziranga Orchid Park", "Majuli", "Kamalabari Satra",
    "Auniati Satra", "Dakhinpat Satra", "Garmur Satra", "Jorhat", "Thengal Manor",
    "Raja Maidam", "Hoollongapar Gibbon Sanctuary", "Sivasagar", "Sivasagar Sivadol",
    "Rang Ghar", "Talatal Ghar", "Kareng Ghar", "Joysagar", "Charaideo Maidams", "Nazira",
    "Dibrugarh", "Dibru-Saikhowa National Park", "Tinsukia", "Digboi", "Digboi Oil Museum",
    "Namdang Stone Bridge", "Margherita", "Ledo", "Dehing Patkai National Park", "Doomdooma",
    "Tezpur", "Agnigarh", "Chitralekha Udyan", "Cole Park", "Da Parbatia Gate", "Bhomoraguri",
    "Nameri National Park", "Bhalukpong", "Jia Bharali River", "Sonitpur Tea Gardens",
    "Haflong", "Jatinga", "Maibong", "Umrangso", "Kopili Reservoir", "Diphu",
    "Karbi Anglong", "Silchar", "Kachari Fort", "Maniharan Tunnel", "Badarpur Fort",
    "Karimganj", "Hailakandi", "Sribhumi", "Digboi War Cemetery", "Panbari Forest",
    "Kakochang Waterfall", "Deoparbat", "Bokakhat", "Raha", "Amoni", "Nagaon",
    "Bordowa Than", "Batadrava", "Barpeta Satra", "Doul Govinda Temple", "Sarthebari",
    "Manas National Park", "Bansbari", "Baksa Hills", "Bhutan Border Villages",
    "Guwahati Planetarium", "Navagraha Hill", "Fancy Bazaar", "Uzanbazar Riverfront",
    "Brahmaputra River Cruise", "Deepor Beel Birding Zone", "Panikhaiti", "Chandrapur",
    "Sonapur Hills", "Barnadi Wildlife Sanctuary"
  ],
  "hidden": [
    "Bogamati", "Kakochang Falls Trail", "Burapahar Forest Trails", "Panbari Reserve Forest",
    "Diphlu River Banks", "Deopahar Archaeological Site", "Nambor Wildlife Sanctuary",
    "Nambor-Doigrung Sanctuary", "Amreng", "Rongjang", "Umsiang", "Langting Mupa Reserve Forest",
    "Jatinga Hillside Villages", "Maibong Waterfall Trails", "Panimur Waterfall", "Haflong Lake Sunrise",
    "Jatinga Birding Area", "Umrangso Hills", "Kopili River Viewpoints", "Panbari Tea Area",
    "Digboi Forest Trails", "Namdang River Gorge", "Bherjan-Borajan-Podumoni Wildlife Sanctuary",
    "Dibru River Islands", "Maguri-Motapung Beel", "Dosam Beel", "Laika Village Area",
    "Bogibeel Riverside", "Dibru-Saikhowa Grasslands", "Negheriting Shiva Doul",
    "Garampani Wildlife Sanctuary", "Singphan Wildlife Sanctuary", "Kakojan", "Gibbon Habitat Trails",
    "Molai Forest Region", "Ahatguri River Island", "Phulbari Ghat", "Bhabananda Devalaya Area",
    "Sarthebari Bell-Metal Village", "Mandia Wetlands", "Barnadi Forest Trails",
    "Raimona Forest Villages", "Kalamati Lake", "Manas Forest Villages", "Bherjan Forest",
    "Rani Reserve Forest", "Chandubi Tribal Villages", "Hatisila Viewpoint",
    "Burachapori Wildlife Sanctuary", "Pobitora Village Trails"
  ]
}

print("Raw data initialized part 1: AP, AR, AS")
