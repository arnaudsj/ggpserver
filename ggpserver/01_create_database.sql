-- phpMyAdmin SQL Dump
-- version 2.11.3deb1ubuntu1.3
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 23, 2010 at 10:42 AM
-- Server version: 5.0.51
-- PHP Version: 5.2.4-2ubuntu5.10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `ggpserver`
--

CREATE DATABASE IF NOT EXISTS `ggpserver` ;

USE `ggpserver` ;

-- --------------------------------------------------------

--
-- Table structure for table `config`
--

CREATE TABLE IF NOT EXISTS `config` (
  `key` varchar(255) NOT NULL,
  `value` varchar(255) default NULL,
  PRIMARY KEY  (`key`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `errormessages`
--

CREATE TABLE IF NOT EXISTS `errormessages` (
  `key` int(11) NOT NULL auto_increment,
  `match_id` varchar(40) NOT NULL,
  `step_number` int(11) NOT NULL,
  `type` varchar(40) NOT NULL,
  `message` varchar(255) NOT NULL,
  `player` varchar(20) default NULL,
  PRIMARY KEY  (`key`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7793 ;

-- --------------------------------------------------------

--
-- Table structure for table `games`
--

CREATE TABLE IF NOT EXISTS `games` (
  `name` varchar(40) NOT NULL,
  `gamedescription` longtext NOT NULL,
  `stylesheet` varchar(255) NOT NULL default '../stylesheets/generic/generic.xsl',
  `xml_view` longtext character set latin1 collate latin1_general_cs,
  `enabled` tinyint(1) NOT NULL default '0',
  `creator` varchar(20) NOT NULL default 'admin',
  `gdl_version` int(11) NOT NULL default '1',
  PRIMARY KEY  (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `matches`
--

CREATE TABLE IF NOT EXISTS `matches` (
  `match_id` varchar(40) NOT NULL,
  `game` varchar(40) NOT NULL,
  `start_clock` int(11) NOT NULL,
  `play_clock` int(11) NOT NULL,
  `start_time` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL default 'new',
  `tournament_id` varchar(40) NOT NULL,
  `scrambled` tinyint(1) NOT NULL default '0',
  `weight` double NOT NULL default '1',
  `owner` varchar(20) NOT NULL default 'admin',
  PRIMARY KEY  (`match_id`),
  KEY `tournament_id` (`tournament_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `match_players`
--

CREATE TABLE IF NOT EXISTS `match_players` (
  `match_id` varchar(40) NOT NULL,
  `player` varchar(20) NOT NULL,
  `roleindex` smallint(6) NOT NULL,
  `goal_value` tinyint(11) default NULL,
  PRIMARY KEY  (`match_id`,`roleindex`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `moves`
--

CREATE TABLE IF NOT EXISTS `moves` (
  `match_id` varchar(40) NOT NULL,
  `step_number` int(11) NOT NULL,
  `roleindex` int(11) NOT NULL,
  `move` text NOT NULL,
  PRIMARY KEY  (`match_id`,`step_number`,`roleindex`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

CREATE TABLE IF NOT EXISTS `players` (
  `name` varchar(20) NOT NULL,
  `host` varchar(255) NOT NULL,
  `port` smallint(6) unsigned NOT NULL,
  `owner` varchar(20) NOT NULL,
  `status` varchar(20) NOT NULL,
  `plays_round_robin` tinyint(1) NOT NULL default '1',
  `plays_manual` tinyint(1) NOT NULL default '1',
  `gdl_version` int(11) NOT NULL default '1',
  PRIMARY KEY  (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `states`
--

CREATE TABLE IF NOT EXISTS `states` (
  `match_id` varchar(40) NOT NULL,
  `step_number` int(11) NOT NULL,
  `state` text NOT NULL,
  `timestamp` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`match_id`,`step_number`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tournaments`
--

CREATE TABLE IF NOT EXISTS `tournaments` (
  `tournament_id` varchar(40) NOT NULL,
  `owner` varchar(20) NOT NULL,
  PRIMARY KEY  (`tournament_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `user_name` varchar(20) NOT NULL,
  `user_pass` varchar(40) NOT NULL,
  `email_address` varchar(320) character set latin1 collate latin1_general_cs default NULL,
  PRIMARY KEY  (`user_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_name`, `user_pass`) VALUES
('admin', '64acb644acc172824a5c0aa3bf1c6686d09ada13');

-- --------------------------------------------------------

--
-- Table structure for table `user_roles`
--

CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_name` varchar(20) NOT NULL,
  `role_name` varchar(20) NOT NULL,
  PRIMARY KEY  (`user_name`,`role_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


--
-- Dumping data for table `user_roles`
--

INSERT INTO `user_roles` (`user_name`, `role_name`) VALUES
('admin', 'admin');
