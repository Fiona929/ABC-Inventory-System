-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 22, 2024 at 09:44 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `groceries`
--

-- --------------------------------------------------------

--
-- Table structure for table `address`
--

CREATE TABLE `address` (
  `addressID` int(11) NOT NULL,
  `street` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `postcode` int(11) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `address`
--

INSERT INTO `address` (`addressID`, `street`, `city`, `state`, `postcode`, `country`) VALUES
(1, 'Jalan Kolombong', 'Kota Kinabalu', 'Sabah', 88300, 'Malaysia'),
(2, 'Jalan Penampang', 'Kota Kinabalu', 'Sabah', 88300, 'Malaysia'),
(3, 'Jalan Tuaran', 'Kota Kinabalu', 'Sabah', 321546, 'Malaysia'),
(4, 'Jalan Tuaran', 'Kota Kinabalu', 'Sabah', 236541, 'Malaysia'),
(5, 'Jalan Hiburan', 'Kota Kinabalu', 'Sabah', 88326, 'Malaysia');

-- --------------------------------------------------------

--
-- Table structure for table `beverages`
--

CREATE TABLE `beverages` (
  `beveragesID` int(11) NOT NULL,
  `productID` int(11) DEFAULT NULL,
  `alcohol` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `food`
--

CREATE TABLE `food` (
  `foodID` int(11) NOT NULL,
  `productID` int(11) NOT NULL,
  `halal` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `inventoryclerk`
--

CREATE TABLE `inventoryclerk` (
  `clerkID` int(11) NOT NULL,
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gender` enum('male','female') DEFAULT NULL,
  `addressID` int(11) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `isManager` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `inventoryclerk`
--

INSERT INTO `inventoryclerk` (`clerkID`, `firstName`, `lastName`, `email`, `gender`, `addressID`, `password`, `isManager`) VALUES
(1, 'Fiona', 'Ng', 'fionang@gmail.com', 'female', 2, '123', 1),
(2, 'Wong', 'Zi Xuan', 'wong@gmail.com', 'male', 5, 'wong', 0);

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `productID` int(11) NOT NULL,
  `productName` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `category` enum('food','beverages') NOT NULL,
  `price` double NOT NULL,
  `halal` tinyint(1) DEFAULT NULL,
  `alcohol` tinyint(1) DEFAULT NULL,
  `supplierID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`productID`, `productName`, `quantity`, `category`, `price`, `halal`, `alcohol`, `supplierID`) VALUES
(3, 'Coke', 215, 'beverages', 3.5, 0, 0, 1),
(4, 'Apple', 195, 'food', 4, 1, 0, 1),
(5, 'Orange', 200, 'food', 4, 1, 0, 1),
(7, 'Potato Chips', 500, 'food', 5, 0, 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `stock`
--

CREATE TABLE `stock` (
  `stockID` int(11) NOT NULL,
  `productID` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `supplier`
--

CREATE TABLE `supplier` (
  `supplierID` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `contactInfo` varchar(255) DEFAULT NULL,
  `addressID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`supplierID`, `name`, `contactInfo`, `addressID`) VALUES
(1, 'John', '0154678952', 1),
(2, 'Cheng', '0132654789', 4);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `address`
--
ALTER TABLE `address`
  ADD PRIMARY KEY (`addressID`);

--
-- Indexes for table `beverages`
--
ALTER TABLE `beverages`
  ADD PRIMARY KEY (`beveragesID`),
  ADD KEY `productID` (`productID`);

--
-- Indexes for table `food`
--
ALTER TABLE `food`
  ADD PRIMARY KEY (`foodID`),
  ADD KEY `productID` (`productID`);

--
-- Indexes for table `inventoryclerk`
--
ALTER TABLE `inventoryclerk`
  ADD PRIMARY KEY (`clerkID`),
  ADD KEY `addressID` (`addressID`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`productID`),
  ADD KEY `supplierID` (`supplierID`);

--
-- Indexes for table `stock`
--
ALTER TABLE `stock`
  ADD PRIMARY KEY (`stockID`),
  ADD KEY `productID` (`productID`);

--
-- Indexes for table `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`supplierID`),
  ADD KEY `addressID` (`addressID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `address`
--
ALTER TABLE `address`
  MODIFY `addressID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `beverages`
--
ALTER TABLE `beverages`
  MODIFY `beveragesID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `food`
--
ALTER TABLE `food`
  MODIFY `foodID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `inventoryclerk`
--
ALTER TABLE `inventoryclerk`
  MODIFY `clerkID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `productID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `stock`
--
ALTER TABLE `stock`
  MODIFY `stockID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `supplier`
--
ALTER TABLE `supplier`
  MODIFY `supplierID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `beverages`
--
ALTER TABLE `beverages`
  ADD CONSTRAINT `beverages_ibfk_1` FOREIGN KEY (`productID`) REFERENCES `product` (`productID`);

--
-- Constraints for table `food`
--
ALTER TABLE `food`
  ADD CONSTRAINT `food_ibfk_1` FOREIGN KEY (`productID`) REFERENCES `product` (`productID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `inventoryclerk`
--
ALTER TABLE `inventoryclerk`
  ADD CONSTRAINT `inventoryclerk_ibfk_1` FOREIGN KEY (`addressID`) REFERENCES `address` (`addressID`);

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `product_ibfk_1` FOREIGN KEY (`supplierID`) REFERENCES `supplier` (`supplierID`);

--
-- Constraints for table `stock`
--
ALTER TABLE `stock`
  ADD CONSTRAINT `stock_ibfk_1` FOREIGN KEY (`productID`) REFERENCES `product` (`productID`);

--
-- Constraints for table `supplier`
--
ALTER TABLE `supplier`
  ADD CONSTRAINT `supplier_ibfk_1` FOREIGN KEY (`addressID`) REFERENCES `address` (`addressID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
