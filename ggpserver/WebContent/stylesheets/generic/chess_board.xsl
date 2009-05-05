<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for printing a rectangular chess board of fixed size
	- For use within <body>.
	- needs a template with name "make_cell_content" which prints the content of a cell, if the cell has an unusual value
	  make_cell_content can take the following arguments:
	  	* xArg
	  	* yArg
	  	* content (the three arguments of the cell fluent)
	  	* piece (the piece name detected automatically)
	  	* background ("light" or "dark" according to the position and if the board is checkered)
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="board.xsl"/>
	<xsl:import href="state.xsl"/>

	<xsl:template name="print_all_chess_boards">
		<xsl:param name="Width">?</xsl:param> <!-- the number of cells per column, if "?" try to detect the width automatically -->
		<xsl:param name="Height">?</xsl:param> <!-- the number of cells per row, if "?" try to detect the height automatically -->
		<xsl:param name="checkered">light</xsl:param>
		<xsl:param name="DefaultCellContent">yes</xsl:param> <!-- use the default img for cell content and only call make_cell_content if value was not recognized -->
		<xsl:param name="xArgIdx">1</xsl:param>
		<xsl:param name="yArgIdx">2</xsl:param>
		<xsl:param name="contentArgIdx">3</xsl:param>
		<xsl:param name="DefaultCell">yes</xsl:param>
		<xsl:param name="CellWidth">48</xsl:param>
		<xsl:param name="CellHeight" select="$CellWidth"/>
		
		<xsl:for-each select="fact">
			<xsl:sort select="prop-f"/>
			<!--<xsl:value-of select="prop-f"/> - <xsl:value-of select="preceding-sibling::fact[1]/prop-f"/><br/>-->
			<xsl:if test="not(prop-f = preceding-sibling::fact[1]/prop-f)">
				<xsl:if test="starts-with(prop-f, 'CELL') and count(arg)=3">
					<xsl:variable name="CellFluentName" select="prop-f"/>
					<xsl:for-each select="..">
						<xsl:call-template name="chess_board">
							<xsl:with-param name="CellFluentName" select="$CellFluentName"/>
							<xsl:with-param name="Width" select="$Width"/>
							<xsl:with-param name="Height" select="$Height"/>
							<xsl:with-param name="checkered" select="$checkered"/>
							<xsl:with-param name="DefaultCellContent" select="$DefaultCellContent"/>
							<xsl:with-param name="xArgIdx" select="$xArgIdx"/>
							<xsl:with-param name="yArgIdx" select="$yArgIdx"/>
							<xsl:with-param name="contentArgIdx" select="$contentArgIdx"/>
							<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
							<xsl:with-param name="CellWidth" select="$CellWidth"/>
							<xsl:with-param name="CellHeight" select="$CellHeight"/>
							<xsl:with-param name="BoardName">
								<xsl:if test="$CellFluentName!='CELL'"><xsl:value-of select="substring-after($CellFluentName, 'CELL')"/></xsl:if>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>

	</xsl:template>
	
	<xsl:template name="print_chess_state">
		<xsl:param name="Width">?</xsl:param> <!-- the number of cells per column, if "?" try to detect the width automatically -->
		<xsl:param name="Height">?</xsl:param> <!-- the number of cells per row, if "?" try to detect the height automatically -->
		<xsl:param name="checkered">light</xsl:param>
		<xsl:param name="DefaultCellContent">yes</xsl:param> <!-- use the default img for cell content and only call make_cell_content if value was not recognized -->
		<xsl:param name="CellFluentName">?</xsl:param> <!-- if "?" try to detect the cell fluent name automatically -->
		<xsl:param name="xArgIdx">1</xsl:param>
		<xsl:param name="yArgIdx">2</xsl:param>
		<xsl:param name="contentArgIdx">3</xsl:param>
		<xsl:param name="DefaultCell">yes</xsl:param>
		<xsl:param name="CellWidth">48</xsl:param>
		<xsl:param name="CellHeight" select="$CellWidth"/>
	
		<xsl:variable name="internalCellFluentName">
			<xsl:choose>
				<xsl:when test="$CellFluentName!='?'"><xsl:value-of select="$CellFluentName"/></xsl:when>
				<xsl:when test="fact[prop-f='CELL' and count(./arg)=3]">CELL</xsl:when>
				<xsl:when test="fact[prop-f='CELLHOLDS' and count(./arg)=3]">CELLHOLDS</xsl:when>
				<xsl:when test="fact[prop-f='LOCATION' and count(./arg)=3]">LOCATION</xsl:when>
				<xsl:when test="fact[count(./arg)=3]">
					<xsl:value-of select="fact[count(arg)=3]/prop-f"/>
				</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:call-template name="chess_board">
			<xsl:with-param name="CellFluentName" select="$internalCellFluentName"/>
			<xsl:with-param name="Width" select="$Width"/>
			<xsl:with-param name="Height" select="$Height"/>
			<xsl:with-param name="checkered" select="$checkered"/>
			<xsl:with-param name="DefaultCellContent" select="$DefaultCellContent"/>
			<xsl:with-param name="xArgIdx" select="$xArgIdx"/>
			<xsl:with-param name="yArgIdx" select="$yArgIdx"/>
			<xsl:with-param name="contentArgIdx" select="$contentArgIdx"/>
			<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
			<xsl:with-param name="CellWidth" select="$CellWidth"/>
			<xsl:with-param name="CellHeight" select="$CellHeight"/>
		</xsl:call-template>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="$internalCellFluentName"/>
		</xsl:call-template>
		
	</xsl:template>
	
	<xsl:template name="chess_board">
		<xsl:param name="Width">?</xsl:param> <!-- the number of cells per column, if "?" try to detect the width automatically -->
		<xsl:param name="Height">?</xsl:param> <!-- the number of cells per row, if "?" try to detect the height automatically -->
		<xsl:param name="checkered">light</xsl:param>
		<xsl:param name="DefaultCellContent">yes</xsl:param> <!-- use the default img for cell content and only call make_cell_content if value was not recognized -->
		<xsl:param name="CellFluentName">?</xsl:param> <!-- if "?" try to detect the cell fluent name automatically -->
		<xsl:param name="xArgIdx">1</xsl:param>
		<xsl:param name="yArgIdx">2</xsl:param>
		<xsl:param name="contentArgIdx">3</xsl:param>
		<xsl:param name="DefaultCell">yes</xsl:param>
		<xsl:param name="CellWidth">48</xsl:param>
		<xsl:param name="CellHeight" select="$CellWidth"/>
		<xsl:param name="BoardName"/>

		<xsl:variable name="internalCellFluentName">
			<xsl:choose>
				<xsl:when test="$CellFluentName!='?'"><xsl:value-of select="$CellFluentName"/></xsl:when>
				<xsl:when test="fact[prop-f='CELL' and count(./arg)=3]">CELL</xsl:when>
				<xsl:when test="fact[prop-f='CELLHOLDS' and count(./arg)=3]">CELLHOLDS</xsl:when>
				<xsl:when test="fact[prop-f='LOCATION' and count(./arg)=3]">LOCATION</xsl:when>
				<xsl:when test="fact[count(./arg)=3]">
					<xsl:value-of select="fact[count(arg)=3]/prop-f"/>
				</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="internalWidth">
			<xsl:choose>
				<xsl:when test="$Width!='?'"><xsl:value-of select="$Width"/></xsl:when>
				<xsl:otherwise>
					<xsl:variable name="ARow">
						<xsl:value-of select="fact[prop-f=$internalCellFluentName]/arg[number($yArgIdx)]"/>
					</xsl:variable>
					<xsl:value-of select="count(fact[prop-f=$internalCellFluentName and arg[number($yArgIdx)]=$ARow])"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="internalHeight">
			<xsl:choose>
				<xsl:when test="$Height!='?'"><xsl:value-of select="$Height"/></xsl:when>
				<xsl:otherwise>
					<xsl:variable name="ACol">
						<xsl:value-of select="fact[prop-f=$internalCellFluentName]/arg[number($xArgIdx)]"/>
					</xsl:variable>
					<xsl:value-of select="count(fact[prop-f=$internalCellFluentName and arg[number($xArgIdx)]=$ACol])"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<style type="text/css" media="all">
			div.chesscellcontent
			{
				position: absolute;
				width:    <xsl:value-of select="$CellWidth - 4"/>px;
				height:   <xsl:value-of select="$CellHeight - 4"/>px;
			}
			div.chess_board
			{
				position: relative;
			}
		</style>

		<div class="chess_board">

			<xsl:call-template name="board">
				<xsl:with-param name="Width" select="$internalWidth"/>
				<xsl:with-param name="Height" select="$internalHeight"/>
				<xsl:with-param name="CellWidth" select="$CellWidth"/>
				<xsl:with-param name="CellHeight" select="$CellHeight"/>
				<xsl:with-param name="checkered" select="$checkered"/>
				<xsl:with-param name="LightCellColor">#ffce9e</xsl:with-param>
				<xsl:with-param name="DarkCellColor">#d18b47</xsl:with-param>
				<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
			</xsl:call-template>

			<xsl:for-each select="fact[prop-f=$internalCellFluentName]">
				<xsl:variable name="xArg" select="./arg[number($xArgIdx)]"/>
				<xsl:variable name="yArg" select="./arg[number($yArgIdx)]"/>
				<xsl:variable name="content" select="./arg[number($contentArgIdx)]"/>

				<xsl:variable name="COORDINATES" select="'12345678ABCDEFGH'"/>
				<xsl:variable name="NUMBERS" select="'1234567812345678'"/>

				<xsl:variable name="x">
					<xsl:choose>
						<xsl:when test="string-length($xArg)>1">
							<xsl:value-of select="number(translate($xArg, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', ''))"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="number(translate($xArg,$COORDINATES,$NUMBERS))"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:variable name="y">
					<xsl:choose>
						<xsl:when test="string-length($yArg)>1">
							<xsl:value-of select="number(translate($yArg, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', ''))"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="number(translate($yArg,$COORDINATES,$NUMBERS))"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:variable name="xPosCell" select="$CellWidth * ($x - 1) + 2"/>
				<xsl:variable name="yPosCell" select="$CellHeight * ($internalHeight - $y) + 2"/>
				<xsl:variable name="CellColor">
					<xsl:choose>
						<xsl:when test="($checkered='dark' and ($x mod 2) + (($internalHeight + 1 - $y) mod 2) = 1) or ($checkered='light' and ($x mod 2) + (($internalHeight + 1 - $y) mod 2) != 1)">dark</xsl:when>
						<xsl:when test="$checkered='alldark'">dark</xsl:when>
						<xsl:otherwise>light</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<div class="chesscellcontent">
					<xsl:attribute name="style">
						left: <xsl:value-of select="$xPosCell"/>px;
						top: <xsl:value-of select="$yPosCell"/>px;
					</xsl:attribute>
					
					<xsl:comment>
						x: <xsl:value-of select="$xArg"/> -&gt; <xsl:value-of select="$x"/>
						y: <xsl:value-of select="$yArg"/> -&gt; <xsl:value-of select="$y"/>
					</xsl:comment>

					<xsl:variable name="piece">
						<xsl:choose>
							<xsl:when test="../fact[prop-f=$internalCellFluentName and arg[number($xArgIdx)]=$xArg and arg[number($yArgIdx)]=$yArg and arg[number($contentArgIdx)]!=$content]">MULTIPLE</xsl:when>
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
							<xsl:when test="$content='BLACK'">O0</xsl:when>
							<xsl:when test="$content='WHITE'">O1</xsl:when>
							<xsl:when test="$content='RED'">O2</xsl:when>
							<xsl:when test="$content='GREEN'">O3</xsl:when>
							<xsl:when test="$content='BLUE'">O4</xsl:when>
							<xsl:when test="$content='CYAN'">O5</xsl:when>
							<xsl:when test="$content='YELLOW'">O6</xsl:when>
							<xsl:when test="$content='PINK'">O7</xsl:when>
							<xsl:when test="$content='BROWN'">O8</xsl:when>
							<xsl:when test="$content='MAGENTA'">O9</xsl:when>
							<xsl:when test="$content='1'">x1</xsl:when>
							<xsl:when test="$content='2'">x2</xsl:when>
							<xsl:when test="$content='3'">x3</xsl:when>
							<xsl:when test="$content='4'">x4</xsl:when>
							<xsl:when test="$content='5'">x5</xsl:when>
							<xsl:when test="$content='6'">x6</xsl:when>
							<xsl:when test="$content='7'">x7</xsl:when>
							<xsl:when test="$content='8'">x8</xsl:when>
							<xsl:when test="$content='9'">x9</xsl:when>
							<xsl:when test="$content='X'">xx</xsl:when>
							<xsl:when test="$content='O'">xo</xsl:when>
						</xsl:choose>
					</xsl:variable>

					<xsl:choose>
						<xsl:when test="$DefaultCellContent!='yes' or $piece=''">
							<xsl:call-template name="make_cell_content">
								<xsl:with-param name="xArg" select="$xArg"/>
								<xsl:with-param name="yArg" select="$yArg"/>
								<xsl:with-param name="content" select="$content"/>
								<xsl:with-param name="piece" select="$piece"/>
								<xsl:with-param name="background" select="$CellColor"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="$piece='BLANK'"/> <!-- empty cell -->
						<xsl:when test="$piece='MULTIPLE'"><b>?</b></xsl:when> <!-- multiple elements in cell -->
						<xsl:otherwise>
							<xsl:call-template name="make_chess_img">
								<xsl:with-param name="piece" select="$piece"/>
								<xsl:with-param name="background" select="$CellColor"/>
								<xsl:with-param name="imgWidth" select="$CellWidth - 4"/>
								<xsl:with-param name="imgHeight" select="$CellHeight - 4"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</xsl:for-each>
			<xsl:if test="$BoardName!=''">
				<p style="text-align: center">Board <xsl:value-of select="$BoardName"/></p>
			</xsl:if>
		</div>
	</xsl:template>


	<!--
		valid pieces are:
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
		<xsl:param name="imgWidth">44</xsl:param>
		<xsl:param name="imgHeight">44</xsl:param>
		
		<img>
			<xsl:attribute name="width"><xsl:value-of select="$imgWidth"/></xsl:attribute>
			<xsl:attribute name="height"><xsl:value-of select="$imgHeight"/></xsl:attribute>
			<xsl:attribute name="src">
				<xsl:value-of select="$stylesheetURL"/>
				<xsl:text>/generic/chess_images/Chess_</xsl:text>
				<xsl:value-of select="$piece"/>
				<xsl:value-of select="substring($background,1,1)"/>
				<xsl:text>44.png</xsl:text>
			</xsl:attribute>
		</img>
	</xsl:template>

</xsl:stylesheet>