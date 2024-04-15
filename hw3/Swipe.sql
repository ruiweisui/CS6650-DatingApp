
CREATE SCHEMA IF NOT EXISTS Swipes;
USE Swipes;

DROP TABLE IF EXISTS Likes;
DROP TABLE IF EXISTS Matches;

CREATE TABLE Likes (
	swiper VARCHAR(255) PRIMARY KEY,
    numOfLikes INT,
    numOfDislikes INT
);

CREATE TABLE Matches (
    swiper VARCHAR(255) PRIMARY KEY,
    matches LONGTEXT
);

