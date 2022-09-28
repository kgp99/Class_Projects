DROP TABLE places;

CREATE TABLE "places" (
	"name"	TEXT NOT NULL UNIQUE,
	"addresstitle"	TEXT,
	"addressstreet"	TEXT,
	"elevation"	REAL,
	"latitude"	REAL NOT NULL,
	"longitude"	REAL NOT NULL,
	"description"	TEXT,
	"category"	TEXT,
	PRIMARY KEY("name")
);

INSERT INTO places VALUES
("ASU-West", "ASU West Campus", "13591 N 47th Ave$Phoenix AZ 85051", 1100.0, 33.608979, -112.159469, "Home of ASU's Applied Computing Program", "School"),
("UAK-Anchorage", "University of Alaska at Anchorage", "290 Spirit Dr$Anchorage AK 99508", 0.0, 61.189748, -149.826721, "University of Alaska's largest campus", "School"),
("Toreros", "University of San Diego", "5998 Alcala Park$San Diego CA 92110", 200.0, 32.771923, -117.188204,"The University of San Diego, a private Catholic undergraduate university.", "School"),
("Barrow-Alaska", "Calgary International Airport", "2000 Airport Rd NE$Calgary AB T2E 6Z8 Canada", 5.0, 71.287881, -156.779417, "The only real way in and out of Barrow Alaska.", "Travel"),
("Calgary-Alberta", "Calgary International Airport", "2000 Airport Rd NE$Calgary AB T2E 6Z8 Canada", 3556.0, 51.131377, -114.011246, "The home of the Calgary Stampede Celebration", "Travel"),
("London-England", "Renaissance London Heathrow Airport", "5 Mondial Way$Harlington Hayes UB3 UK", 82.0, 51.481959, -0.445286, "Renaissance Hotel at the Heathrow Airport", "Travel"),
("Moscow-Russia", "Courtyard Moscow City Center", "Voznesenskiy per 7 $ Moskva Russia 125009", 512.0, 55.758666, 37.604058, "The Marriott Courtyard in downtown Moscow", "Travel" ),
("New-York-NY", "New York City Hall", "1 Elk St$New York NY 10007", 2.0, 40.712991, -74.005948, "New York City Hall at West end of Brooklyn Bridge", "Travel"),
("Notre-Dame-Paris", "Cathedral Notre Dame de Paris", "6 Parvis Notre-Dame Pl Jean-Paul-II$75004 Paris France", 115.0, 48.852972, 2.349910, "The 13th century cathedral with gargoyles, one of the first flying buttress, and home of the purported crown of thorns.",  "Travel" ),
("Circlestone", "", "", 6000.0, 33.477524, -111.134345, "Indian Ruins located on the second highest peak in the Superstition Wilderness of the Tonto National Forest. Leave Fireline at Turney Spring (33.487610,-111.132400)",  "Hike"),
("Reavis-Ranch", "", "",  5000.0, 33.491154,  -111.155385, "Historic Ranch in Superstition Mountains famous for Apple orchards", "Hike"),
("Rogers-Trailhead", "", "", 4500.0, 33.422212, -111.173393, "Trailhead for hiking to Rogers Canyon Ruins and Reavis Ranch", "Hike"),
("Reavis-Grave", "", "", 3900.0, 33.441499, -111.182511, "Grave site of Reavis Ranch Proprietor.", "Hike"),
("Muir-Woods", "Muir Woods National Monument", "1 Muir Woods Rd$Mill Valley CA 94941", 350.0, 37.8912, -122.5957, "Redwood forest North of San Francisco, surrounded by Mount Tamalpais State Park.",  "Hike"),
("Windcave-Peak", "Usery Mountain Recreation Area", "3939 N Usery Pass Rd$Mesa AZ 85207", 3130.0, 33.476069, -111.595709, "Beyond the Wind Cave is a half mile trail with 250' additional elevation to the peak overlooking Usery and the Valley.", "Hike");