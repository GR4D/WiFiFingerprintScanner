-- phpMyAdmin SQL Dump
-- version 5.0.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Czas generowania: 09 Gru 2020, 22:10
-- Wersja serwera: 10.4.14-MariaDB
-- Wersja PHP: 7.4.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `wifi_fingerprint_db`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `access_points`
--

CREATE TABLE `access_points` (
  `mac` varchar(17) COLLATE utf8_polish_ci NOT NULL,
  `ssid` text COLLATE utf8_polish_ci DEFAULT NULL,
  `frequency` int(11) DEFAULT NULL,
  `channel` int(11) DEFAULT NULL,
  `channel_width` int(11) DEFAULT NULL,
  `first_seen` text COLLATE utf8_polish_ci DEFAULT NULL,
  `comments` text COLLATE utf8_polish_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `fingerprints`
--

CREATE TABLE `fingerprints` (
  `id` int(11) NOT NULL,
  `x_pos` int(11) NOT NULL,
  `y_pos` int(11) NOT NULL,
  `band` int(11) NOT NULL,
  `ap_1` varchar(17) COLLATE utf8_polish_ci NOT NULL,
  `ss_1` int(11) NOT NULL,
  `ap_2` varchar(17) COLLATE utf8_polish_ci DEFAULT NULL,
  `ss_2` int(11) DEFAULT NULL,
  `ap_3` varchar(17) COLLATE utf8_polish_ci DEFAULT NULL,
  `ss_3` int(11) DEFAULT NULL,
  `ap_4` varchar(17) COLLATE utf8_polish_ci DEFAULT NULL,
  `ss_4` int(11) DEFAULT NULL,
  `ap_5` varchar(17) COLLATE utf8_polish_ci DEFAULT NULL,
  `ss_5` int(11) DEFAULT NULL,
  `ap_6` varchar(17) COLLATE utf8_polish_ci DEFAULT NULL,
  `ss_6` int(11) DEFAULT NULL,
  `date` text COLLATE utf8_polish_ci NOT NULL,
  `device_info` text COLLATE utf8_polish_ci NOT NULL,
  `device_mac` varchar(17) COLLATE utf8_polish_ci NOT NULL,
  `orientation` text COLLATE utf8_polish_ci NOT NULL,
  `fingerprint_area` text COLLATE utf8_polish_ci NOT NULL,
  `comments` text COLLATE utf8_polish_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `access_points`
--
ALTER TABLE `access_points`
  ADD PRIMARY KEY (`mac`),
  ADD KEY `mac` (`mac`);

--
-- Indeksy dla tabeli `fingerprints`
--
ALTER TABLE `fingerprints`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ap_1` (`ap_1`,`ap_2`,`ap_3`,`ap_4`,`ap_5`,`ap_6`),
  ADD KEY `ap_2` (`ap_2`),
  ADD KEY `ap_3` (`ap_3`),
  ADD KEY `ap_4` (`ap_4`),
  ADD KEY `ap_5` (`ap_5`),
  ADD KEY `ap_6` (`ap_6`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `fingerprints`
--
ALTER TABLE `fingerprints`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=146;

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `fingerprints`
--
ALTER TABLE `fingerprints`
  ADD CONSTRAINT `fingerprints_ibfk_1` FOREIGN KEY (`ap_1`) REFERENCES `access_points` (`mac`),
  ADD CONSTRAINT `fingerprints_ibfk_2` FOREIGN KEY (`ap_2`) REFERENCES `access_points` (`mac`),
  ADD CONSTRAINT `fingerprints_ibfk_3` FOREIGN KEY (`ap_3`) REFERENCES `access_points` (`mac`),
  ADD CONSTRAINT `fingerprints_ibfk_4` FOREIGN KEY (`ap_4`) REFERENCES `access_points` (`mac`),
  ADD CONSTRAINT `fingerprints_ibfk_5` FOREIGN KEY (`ap_5`) REFERENCES `access_points` (`mac`),
  ADD CONSTRAINT `fingerprints_ibfk_6` FOREIGN KEY (`ap_6`) REFERENCES `access_points` (`mac`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
