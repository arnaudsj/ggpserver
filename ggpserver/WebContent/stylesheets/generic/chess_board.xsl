<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for printing a rectangular chess board of fixed size
	- For use within <body>.
	- Accepts args: xPos and yPos which specify absolute position.
	- needs a template with name "make_cell_content" which prints the content of a cell, if the cell has an unusual value
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:import href="board.xsl"/>
	
	<xsl:template name="chess_board">
		<xsl:param name="xPos"/>
		<xsl:param name="yPos"/>
		<xsl:param name="Width"/> <!-- the number of cells per column -->
		<xsl:param name="Height"/> <!-- the number of cells per row -->
		<xsl:param name="checkered">light</xsl:param>
		<xsl:param name="DefaultCellContent">yes</xsl:param> <!-- use the default img for cell content and only call make_cell_content if value was not recognized -->

		<style type="text/css" media="all">
			div.chesscellcontent
			{
				position: absolute;
				width:44px;
				height:44px;
			}
		</style>
		
		<xsl:call-template name="board">
			<xsl:with-param name="xPos" select="$xPos"/>
			<xsl:with-param name="yPos" select="$yPos"/>
			<xsl:with-param name="Width" select="$Width"/>
			<xsl:with-param name="Height" select="$Height"/>
			<xsl:with-param name="CellWidth">48</xsl:with-param>
			<xsl:with-param name="checkered" select="$checkered"/>
			<xsl:with-param name="LightCellColor">#ffce9e</xsl:with-param>
			<xsl:with-param name="DarkCellColor">#d18b47</xsl:with-param>
		</xsl:call-template>
		
		<xsl:variable name="CellFluentName">
			<xsl:choose>
				<xsl:when test="/match/state/fact[prop-f='CELL' and count(./arg)=3]">CELL</xsl:when>
				<xsl:when test="/match/state/fact[prop-f='LOCATION' and count(./arg)=3]">LOCATION</xsl:when>
				<xsl:when test="/match/state/fact[count(./arg)=3]">
					<xsl:value-of select="/match/state/fact[count(arg)=3]/prop-f"/>
				</xsl:when>
				<xsl:otherwise>NONE</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:for-each select="/match/state/fact[prop-f=$CellFluentName]">
			<xsl:variable name="xArg" select="./arg[1]"/>
			<xsl:variable name="yArg" select="./arg[2]"/>
			<xsl:variable name="content" select="./arg[3]"/>
			
			<xsl:variable name="COORDINATES" select="'12345678ABCDEFGH'"/>
			<xsl:variable name="NUMBERS" select="'1234567812345678'"/>

			<xsl:variable name="x">
				<xsl:choose>
					<xsl:when test="string-length($xArg)>1">
						<xsl:value-of select="translate($xArg, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', '')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="translate($xArg,$COORDINATES,$NUMBERS)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="y">
				<xsl:choose>
					<xsl:when test="string-length($yArg)>1">
						<xsl:value-of select="translate($xArg, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', '')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="translate($yArg,$COORDINATES,$NUMBERS)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="xPosCell" select="48 * ($x - 1) + 2 + $xPos"/>
			<xsl:variable name="yPosCell" select="48 * ($Height - $y) + 2 + $yPos"/>
			<xsl:variable name="CellColor">
				<xsl:choose>
					<xsl:when test="($checkered='dark' and ($x mod 2) + (($Height + 1 - $y) mod 2) != 1) or ($checkered='light' and ($x mod 2) + (($Height + 1 - $y) mod 2) = 1)">dark</xsl:when>
					<xsl:otherwise>light</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<div class="chesscellcontent">
				<xsl:attribute name="style">
					left: <xsl:value-of select="$xPosCell"/>px;
					top: <xsl:value-of select="$yPosCell"/>px;
				</xsl:attribute>
				
				<xsl:variable name="piece">
					<xsl:choose>
						<xsl:when test="not ($DefaultCellContent = 'yes')"/> <!-- empty value -->
						<xsl:when test="$content='B' or $content='BLANK'">BLANK</xsl:when>
						<xsl:when test="$content='KNIGHT' or $content='WHITEKNIGHT' or $content='WN'">nl</xsl:when>
						<xsl:when test="$content='PAWN' or $content='WHITEPAWN' or $content='WP'">pl</xsl:when>
						<xsl:when test="$content='ROOK' or $content='WHITEROOK' or $content='WR'">rl</xsl:when>
						<xsl:when test="$content='BISHOP' or $content='WHITEBISHOP' or $content='WB'">bl</xsl:when>
						<xsl:when test="$content='QUEEN' or $content='WHITEQUEEN' or $content='WQ'">ql</xsl:when>
						<xsl:when test="$content='KING' or $content='WHITEKING' or $content='WK'">kl</xsl:when>
						<xsl:when test="$content='BLACKKNIGHT' or $content='BN'">nd</xsl:when>
						<xsl:when test="$content='BLACKPAWN' or $content='BP'">pd</xsl:when>
						<xsl:when test="$content='BLACKROOK' or $content='BR'">rd</xsl:when>
						<xsl:when test="$content='BLACKBISHOP' or $content='BB'">bd</xsl:when>
						<xsl:when test="$content='BLACKQUEEN' or $content='BQ'">qd</xsl:when>
						<xsl:when test="$content='BLACKKING' or $content='BK'">kd</xsl:when>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:choose>
					<xsl:when test="$piece='BLANK'"/> <!-- empty cell -->
					<xsl:when test="$piece=''">
						<xsl:call-template name="make_cell_content">
							<xsl:with-param name="content" select="$content"/>
							<xsl:with-param name="background" select="$CellColor"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="make_chess_img">
							<xsl:with-param name="piece" select="$piece"/>
							<xsl:with-param name="background" select="$CellColor"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:for-each>
	</xsl:template>

	
	
	<!-- valid pieces are:
			[abcdefghkmnpqrsz][dl] - (piecename+color)
			O[0..9]                - circles in different colors 
			x[1..9]                - numbers 1 to 9
			x[owx]                 - small black and white circles and x
			''                     - empty square
			[jD][01]			   - single and double checkers pieces in black and white
		background is either 'light' or 'dark'
	-->

	<xsl:template name="make_chess_img">
		<xsl:param name="piece"/>
		<xsl:param name="background">light</xsl:param>

		<img width="44" height="44">
			<xsl:attribute name="src">
				<xsl:text>../stylesheets/generic/chess_images/Chess_</xsl:text>
				<xsl:value-of select="$piece"/>
				<xsl:value-of select="substring($background,1,1)"/>
				<xsl:text>44.png</xsl:text>
			</xsl:attribute>
		</img>
	</xsl:template>

</xsl:stylesheet>