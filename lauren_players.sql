-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Tempo de geração: 01-Out-2020 às 22:23
-- Versão do servidor: 10.1.46-MariaDB
-- versão do PHP: 7.3.22

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `s1358_lauren`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `lauren_players`
--

CREATE TABLE `lauren_players` (
  `id` varchar(18) NOT NULL,
  `data` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Extraindo dados da tabela `lauren_players`
--

INSERT INTO `lauren_players` (`id`, `data`) VALUES
('233689502240079872', '{\"winMatches\":[],\"userID\":233689502240079872,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":396,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('272879983326658570', '{\"winMatches\":[],\"userID\":272879983326658570,\"dailyDelay\":1601682471343,\"level\":29,\"money\":293,\"experience\":29300,\"ludoRank\":\"SILVERII\",\"poolRank\":\"NOTHING\",\"ludoPoints\":15,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('277948117788131328', '{\"winMatches\":[],\"userID\":277948117788131328,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":60,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('278632391181074442', '{\"winMatches\":[],\"userID\":278632391181074442,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":84,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('295672615211761664', '{\"winMatches\":[],\"userID\":295672615211761664,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":66,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('299984190898831361', '{\"winMatches\":[],\"userID\":299984190898831361,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":63,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('311589767336689664', '{\"winMatches\":[],\"userID\":311589767336689664,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":360,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('371365457640292357', '{\"winMatches\":[],\"userID\":371365457640292357,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":12,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('375329659438759939', '{\"winMatches\":[],\"userID\":375329659438759939,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":33,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('375773609303605249', '{\"winMatches\":[],\"userID\":375773609303605249,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":12,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('383595359248711682', '{\"winMatches\":[],\"userID\":383595359248711682,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":12,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('388385602032369675', '{\"winMatches\":[],\"userID\":388385602032369675,\"dailyDelay\":1601187156834,\"level\":0,\"money\":115,\"experience\":403,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('395603917163200512', '{\"winMatches\":[],\"userID\":395603917163200512,\"dailyDelay\":0,\"level\":2,\"money\":100,\"experience\":2195,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('402146757636653067', '{\"winMatches\":[],\"userID\":402146757636653067,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":18,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('409747242283565056', '{\"winMatches\":[],\"userID\":409747242283565056,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('411265670798376970', '{\"winMatches\":[],\"userID\":411265670798376970,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":18,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('448042979425255427', '{\"winMatches\":[],\"userID\":448042979425255427,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":714,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('455353234656198677', '{\"winMatches\":[],\"userID\":455353234656198677,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('464229207032201218', '{\"winMatches\":[],\"userID\":464229207032201218,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":15,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('535824794210271238', '{\"userID\":535824794210271238,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0,\"winMatches\":[]}'),
('542690760105918464', '{\"alarmsName\":[],\"winMatches\":[],\"userID\":542690760105918464,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('549392914480889859', '{\"winMatches\":[],\"userID\":549392914480889859,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('552690531805954088', '{\"winMatches\":[],\"userID\":552690531805954088,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('563848035826794517', '{\"winMatches\":[],\"userID\":563848035826794517,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":162,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('564602529816510464', '{\"winMatches\":[],\"userID\":564602529816510464,\"dailyDelay\":1592770586570,\"level\":1,\"money\":115,\"experience\":1013,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('579477590696394772', '{\"winMatches\":[],\"userID\":579477590696394772,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('581190363511783424', '{\"winMatches\":[],\"userID\":581190363511783424,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":18,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('600515115632295985', '{\"winMatches\":[],\"userID\":600515115632295985,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":795,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('609190838983196673', '{\"alarmsName\":[],\"winMatches\":[],\"userID\":609190838983196673,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":3,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('625409700506370058', '{\"winMatches\":[],\"userID\":625409700506370058,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('628972959201230860', '{\"winMatches\":[],\"userID\":628972959201230860,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('641796734636523560', '{\"winMatches\":[],\"userID\":641796734636523560,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":3,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('670135208133132308', '{\"winMatches\":[],\"userID\":670135208133132308,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":81,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('673577362440585287', '{\"winMatches\":[],\"userID\":673577362440585287,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('689689438326226992', '{\"winMatches\":[],\"userID\":689689438326226992,\"dailyDelay\":0,\"level\":1,\"money\":100,\"experience\":1170,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('694382502038470737', '{\"winMatches\":[],\"userID\":694382502038470737,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":9,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('694484255962693703', '{\"winMatches\":[],\"userID\":694484255962693703,\"dailyDelay\":0,\"level\":9,\"money\":80,\"experience\":9414,\"ludoRank\":\"SILVERII\",\"poolRank\":\"NOTHING\",\"ludoPoints\":15,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('699071720090501191', '{\"winMatches\":[],\"userID\":699071720090501191,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":3,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('699729769314648144', '{\"winMatches\":[],\"userID\":699729769314648144,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":762,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('701831084241059950', '{\"winMatches\":[],\"userID\":701831084241059950,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('704680244774305802', '{\"winMatches\":[],\"userID\":704680244774305802,\"dailyDelay\":1601683997828,\"level\":8,\"money\":190,\"experience\":8410,\"ludoRank\":\"NOTHING\",\"poolRank\":\"SILVERIII\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":20,\"poolWins\":0,\"poolMatches\":0}'),
('710164668823633991', '{\"winMatches\":[],\"userID\":710164668823633991,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":33,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('715408113993384020', '{\"winMatches\":[],\"userID\":715408113993384020,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":9,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('717420048007823492', '{\"winMatches\":[],\"userID\":717420048007823492,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('723615935348342936', '{\"winMatches\":[],\"userID\":723615935348342936,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('735527468521226241', '{\"alarmsName\":[],\"winMatches\":[],\"userID\":735527468521226241,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('742778760826191913', '{\"winMatches\":[],\"userID\":742778760826191913,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('743996920858673267', '{\"winMatches\":[],\"userID\":743996920858673267,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('749042138683277385', '{\"alarmsName\":[],\"winMatches\":[],\"userID\":749042138683277385,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":0,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('753620446410440864', '{\"winMatches\":[],\"userID\":753620446410440864,\"dailyDelay\":0,\"level\":2,\"money\":100,\"experience\":2042,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}'),
('758889276963946507', '{\"winMatches\":[],\"userID\":758889276963946507,\"dailyDelay\":0,\"level\":0,\"money\":100,\"experience\":72,\"ludoRank\":\"NOTHING\",\"poolRank\":\"NOTHING\",\"ludoPoints\":0,\"ludoWins\":0,\"ludoMatches\":0,\"poolPoints\":0,\"poolWins\":0,\"poolMatches\":0}');

--
-- Índices para tabelas despejadas
--

--
-- Índices para tabela `lauren_players`
--
ALTER TABLE `lauren_players`
  ADD PRIMARY KEY (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
