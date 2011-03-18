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
	<xsl:import href="chess_board_play_link.xsl"/>

	<!--
		print_all_chess_boards prints a chessboard for all detected board fluents.
		At the moment ternary fluents with a name CELL* are considered as boards.
	-->
	
	<xsl:key name="by-fluent" match="fact" use="prop-f"/>
	
	<xsl:template name="print_all_chess_boards">
		<xsl:param name="Width">?</xsl:param> <!-- the number of cells per row, if "?" try to detect the width automatically -->
		<xsl:param name="Height">?</xsl:param> <!-- the number of cells per column, if "?" try to detect the height automatically -->
		<xsl:param name="MinX">1</xsl:param> <!-- the lowest x coordinate (as number) -->
		<xsl:param name="MinY">1</xsl:param> <!-- the lowest y coordinate (as number) -->
		<xsl:param name="checkered">light</xsl:param>
		<xsl:param name="DefaultCellContent">yes</xsl:param> <!-- use the default img for cell content and only call make_cell_content if value was not recognized -->
		<xsl:param name="xArgIdx">1</xsl:param>
		<xsl:param name="yArgIdx">2</xsl:param>
		<xsl:param name="contentArgIdx">3</xsl:param>
		<xsl:param name="mirrorY">no</xsl:param>
		<xsl:param name="DefaultCell">yes</xsl:param>
		<xsl:param name="BorderWidth">2</xsl:param> <!-- the width of the boarder around each cell in px -->
		<xsl:param name="BorderStyle">solid #FFC</xsl:param>

		<xsl:param name="CellWidth" select="44 + 2 * $BorderWidth"/>
		<xsl:param name="CellHeight" select="$CellWidth"/>
		
		<xsl:call-template name="selectCellJavaScript">
			<xsl:with-param name="xArgIdx" select="$xArgIdx" />
			<xsl:with-param name="yArgIdx" select="$yArgIdx" />
		</xsl:call-template>
		
		<xsl:for-each select="fact">
			<xsl:sort select="prop-f"/>
			<xsl:if test="generate-id(.) = generate-id(key('by-fluent', prop-f)[1])">
				<xsl:comment>
					fluent: (<xsl:value-of select="prop-f"/> <xsl:for-each select="arg"> <xsl:value-of select="."/> </xsl:for-each>)
				</xsl:comment>
				<xsl:if test="starts-with(prop-f, 'CELL') and count(arg)=3">
					<xsl:variable name="CellFluentName" select="prop-f"/>
					<xsl:for-each select="..">
						<xsl:call-template name="chess_board">
							<xsl:with-param name="CellFluentName" select="$CellFluentName"/>
							<xsl:with-param name="Width" select="$Width"/>
							<xsl:with-param name="Height" select="$Height"/>
							<xsl:with-param name="MinX" select="$MinX"/>
							<xsl:with-param name="MinY" select="$MinY"/>
							<xsl:with-param name="checkered" select="$checkered"/>
							<xsl:with-param name="DefaultCellContent" select="$DefaultCellContent"/>
							<xsl:with-param name="xArgIdx" select="$xArgIdx"/>
							<xsl:with-param name="yArgIdx" select="$yArgIdx"/>
							<xsl:with-param name="contentArgIdx" select="$contentArgIdx"/>
							<xsl:with-param name="mirrorY" select="$mirrorY"/>
							<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
							<xsl:with-param name="CellWidth" select="$CellWidth"/>
							<xsl:with-param name="CellHeight" select="$CellHeight"/>
							<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
							<xsl:with-param name="BorderStyle" select="$BorderStyle"/>
							<xsl:with-param name="BoardName">
								<xsl:if test="$CellFluentName!='CELL'"><xsl:value-of select="substring-after($CellFluentName, 'CELL')"/></xsl:if>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<!--
		print_chess_state prints a chess board and a list with the remaining fluents.
	-->
	<xsl:template name="print_chess_state">
		<xsl:param name="Width">?</xsl:param> <!-- the number of cells per row, if "?" try to detect the width automatically -->
		<xsl:param name="Height">?</xsl:param> <!-- the number of cells per column, if "?" try to detect the height automatically -->
		<xsl:param name="MinX">1</xsl:param> <!-- the lowest x coordinate (as number) -->
		<xsl:param name="MinY">1</xsl:param> <!-- the lowest y coordinate (as number) -->
		<xsl:param name="checkered">light</xsl:param>
		<xsl:param name="DefaultCellContent">yes</xsl:param> <!-- use the default img for cell content and only call make_cell_content if value was not recognized -->
		<xsl:param name="CellFluentName">?</xsl:param> <!-- if "?" try to detect the cell fluent name automatically -->
		<xsl:param name="xArgIdx">1</xsl:param>
		<xsl:param name="yArgIdx">2</xsl:param>
		<xsl:param name="contentArgIdx">3</xsl:param>
		<xsl:param name="mirrorY">no</xsl:param>
		<xsl:param name="DefaultCell">yes</xsl:param>
		<xsl:param name="BorderWidth">2</xsl:param> <!-- the width of the boarder around each cell in px -->
		<xsl:param name="BorderStyle">solid #FFC</xsl:param>
		<xsl:param name="CellWidth" select="44 + 2 * $BorderWidth"/>
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
		
		<xsl:call-template name="selectCellJavaScript">
			<xsl:with-param name="xArgIdx" select="$xArgIdx" />
			<xsl:with-param name="yArgIdx" select="$yArgIdx" />
		</xsl:call-template>

		<xsl:call-template name="chess_board">
			<xsl:with-param name="CellFluentName" select="$internalCellFluentName"/>
			<xsl:with-param name="Width" select="$Width"/>
			<xsl:with-param name="Height" select="$Height"/>
			<xsl:with-param name="MinX" select="$MinX"/>
			<xsl:with-param name="MinY" select="$MinY"/>
			<xsl:with-param name="checkered" select="$checkered"/>
			<xsl:with-param name="DefaultCellContent" select="$DefaultCellContent"/>
			<xsl:with-param name="xArgIdx" select="$xArgIdx"/>
			<xsl:with-param name="yArgIdx" select="$yArgIdx"/>
			<xsl:with-param name="contentArgIdx" select="$contentArgIdx"/>
			<xsl:with-param name="mirrorY" select="$mirrorY"/>
			<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
			<xsl:with-param name="CellWidth" select="$CellWidth"/>
			<xsl:with-param name="CellHeight" select="$CellHeight"/>
			<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
			<xsl:with-param name="BorderStyle" select="$BorderStyle"/>
		</xsl:call-template>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="$internalCellFluentName"/>
		</xsl:call-template>
	</xsl:template>
	
	<!--
		chess_board prints a chess board from a selected or automatically detected board fluent.
	-->
	<xsl:template name="chess_board">
		<xsl:param name="Width">?</xsl:param> <!-- the number of cells per row, if "?" try to detect the width automatically -->
		<xsl:param name="Height">?</xsl:param> <!-- the number of cells per column, if "?" try to detect the height automatically -->
		<xsl:param name="MinX">1</xsl:param> <!-- the lowest x coordinate (as number) -->
		<xsl:param name="MinY">1</xsl:param> <!-- the lowest y coordinate (as number) -->
		<xsl:param name="checkered">light</xsl:param> <!-- "invisible" is not supported for cells that do have a content, light background will be used instead -->
		<xsl:param name="DefaultCellContent">yes</xsl:param> <!-- use the default img for cell content and only call make_cell_content if value was not recognized -->
		<xsl:param name="CellFluentName">?</xsl:param> <!-- if "?" try to detect the cell fluent name automatically -->
		<xsl:param name="xArgIdx">1</xsl:param>
		<xsl:param name="yArgIdx">2</xsl:param>
		<xsl:param name="contentArgIdx">3</xsl:param>
		<xsl:param name="mirrorY">no</xsl:param>
		<xsl:param name="DefaultCell">yes</xsl:param>
		<xsl:param name="BorderWidth">2</xsl:param> <!-- the width of the boarder around each cell in px -->
		<xsl:param name="BorderStyle">solid #FFC</xsl:param>
		<xsl:param name="CellWidth" select="44 + 2 * $BorderWidth"/>
		<xsl:param name="CellHeight" select="$CellWidth"/>
		<xsl:param name="BoardName"/>

		<!-- try detect board fluent if it wasn't given as parameter -->
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
		
		<!-- detect board size and lowest coordinate if $Width='?' (or $Height='?') -->
		<xsl:variable name="internalMinX">
			<xsl:choose>
				<xsl:when test="$Width!='?'"><xsl:value-of select="$MinX"/></xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getMinCoord">
						<xsl:with-param name="cellFluentName" select="$internalCellFluentName"/>
						<xsl:with-param name="coordArgIdx" select="$xArgIdx"/>
						<xsl:with-param name="otherArgIdx" select="$yArgIdx"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="internalWidth">
			<xsl:choose>
				<xsl:when test="$Width!='?'"><xsl:value-of select="$Width"/></xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getDimension">
						<xsl:with-param name="cellFluentName" select="$internalCellFluentName"/>
						<xsl:with-param name="coordArgIdx" select="$xArgIdx"/>
						<xsl:with-param name="otherArgIdx" select="$yArgIdx"/>
						<xsl:with-param name="MinCoord" select="$internalMinX"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="internalMinY">
			<xsl:choose>
				<xsl:when test="$Height!='?'"><xsl:value-of select="$MinY"/></xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getMinCoord">
						<xsl:with-param name="cellFluentName" select="$internalCellFluentName"/>
						<xsl:with-param name="coordArgIdx" select="$yArgIdx"/>
						<xsl:with-param name="otherArgIdx" select="$xArgIdx"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="internalHeight">
			<xsl:choose>
				<xsl:when test="$Height!='?'"><xsl:value-of select="$Height"/></xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getDimension">
						<xsl:with-param name="cellFluentName" select="$internalCellFluentName"/>
						<xsl:with-param name="coordArgIdx" select="$yArgIdx"/>
						<xsl:with-param name="otherArgIdx" select="$xArgIdx"/>
						<xsl:with-param name="MinCoord" select="$internalMinY"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<style type="text/css" media="all">
			div.chesscellcontent
			{
				position: absolute;
				width:    <xsl:value-of select="$CellWidth - 2 * $BorderWidth"/>px;
				height:   <xsl:value-of select="$CellHeight - 2 * $BorderWidth"/>px;
			}
			div.chess_board
			{
				position: relative;
			}
			a div.chesscellcontent img {
				border-style:		none;
			}
		</style>

		<div class="chess_board">
			<!-- print the raw board -->
			<xsl:call-template name="board">
				<xsl:with-param name="Width" select="$internalWidth"/>
				<xsl:with-param name="Height" select="$internalHeight"/>
				<xsl:with-param name="CellWidth" select="$CellWidth"/>
				<xsl:with-param name="CellHeight" select="$CellHeight"/>
				<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
				<xsl:with-param name="BorderStyle" select="$BorderStyle"/>
				<xsl:with-param name="checkered" select="$checkered"/>
				<xsl:with-param name="LightCellColor">#ffce9e</xsl:with-param>
				<xsl:with-param name="DarkCellColor">#d18b47</xsl:with-param>
				<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
			</xsl:call-template>

			<!-- for each cell print the content -->
			<xsl:for-each select="fact[prop-f=$internalCellFluentName]">
				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				
				<xsl:variable name="xArg" select="./arg[number($xArgIdx)]"/>
				<xsl:variable name="yArg" select="./arg[number($yArgIdx)]"/>
				<xsl:variable name="content" select="./arg[number($contentArgIdx)]"/>

				<!-- compute numeric x,y coordinates from the fluent's arguments -->
				<xsl:variable name="xArgNumber">
					<xsl:call-template name="coord2number">
						<xsl:with-param name="coord" select="$xArg"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="x" select="$xArgNumber - $internalMinX + 1"/>

				<xsl:variable name="yArgNumber">
					<xsl:call-template name="coord2number">
						<xsl:with-param name="coord" select="$yArg"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="y" select="$yArgNumber - $internalMinY + 1"/>

				<xsl:variable name="xPosCell" select="$CellWidth * ($x - 1) + $BorderWidth"/>
				<xsl:variable name="yPosCell">
					<xsl:choose>
						<xsl:when test="$mirrorY='yes'"><xsl:value-of select="$CellHeight * ($y - 1) + $BorderWidth"/></xsl:when>
						<xsl:otherwise><xsl:value-of select="$CellHeight * ($internalHeight - $y) + $BorderWidth"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<!-- select the right background color for the cell based on the coordinates -->
				<xsl:variable name="CellColor">
					<xsl:choose>
						<xsl:when test="($checkered='dark' and ($x mod 2) + (($internalHeight + 1 - $y) mod 2) != 1) or ($checkered='light' and ($x mod 2) + (($internalHeight + 1 - $y) mod 2) = 1)">dark</xsl:when>
						<xsl:when test="$checkered='alldark'">dark</xsl:when>
						<xsl:otherwise>light</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:comment>
					x: <xsl:value-of select="$xArg"/> -&gt; <xsl:value-of select="$x"/>
					y: <xsl:value-of select="$yArg"/> -&gt; <xsl:value-of select="$y"/>
				</xsl:comment>
				
				<!-- select the default image for the cell content -->
					<xsl:variable name="piece">
						<xsl:choose>
							<xsl:when test="../fact[prop-f=$internalCellFluentName and arg[number($xArgIdx)]=$xArg and arg[number($yArgIdx)]=$yArg and arg[number($contentArgIdx)]!=$content]">MULTIPLE</xsl:when>
							<xsl:when test="$content='B' or $content='BLANK'"/>
							<xsl:when test="$content='K' or $content='KNIGHT' or $content='WHITEKNIGHT' or $content='WN'">nl</xsl:when>
							<xsl:when test="$content='P' or $content='PAWN' or $content='WHITEPAWN' or $content='WP'">pl</xsl:when>
							<xsl:when test="$content='R' or $content='ROOK' or $content='WHITEROOK' or $content='WR'">rl</xsl:when>
							<xsl:when test="$content='B' or $content='BISHOP' or $content='WHITEBISHOP' or $content='WB'">bl</xsl:when>
							<xsl:when test="$content='Q' or $content='QUEEN' or $content='WHITEQUEEN' or $content='WQ'">ql</xsl:when>
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
							<xsl:when test="$content='CYAN' or $content='TEAL'">O5</xsl:when>
							<xsl:when test="$content='YELLOW'">O6</xsl:when>
							<xsl:when test="$content='PINK'">O7</xsl:when>
							<xsl:when test="$content='BROWN' or $content='ORANGE'">O8</xsl:when>
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
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="contains($content, 'KNIGHT')">n</xsl:when>
									<xsl:when test="contains($content, 'PAWN')">p</xsl:when>
									<xsl:when test="contains($content, 'ROOK')">r</xsl:when>
									<xsl:when test="contains($content, 'BISHOP')">b</xsl:when>
									<xsl:when test="contains($content, 'QUEEN')">q</xsl:when>
									<xsl:when test="contains($content, 'KING')">k</xsl:when>
									<xsl:when test="contains($content, 'CHECKER')">j</xsl:when>
									<xsl:when test="contains($content, 'DISC')">O</xsl:when>
									<xsl:otherwise>UNKNOWN</xsl:otherwise>
								</xsl:choose>
								<xsl:choose>
									<xsl:when test="contains($content, 'BLACK')">0</xsl:when>
									<xsl:when test="contains($content, 'WHITE')">1</xsl:when>
									<xsl:when test="contains($content, 'RED')">2</xsl:when>
									<xsl:when test="contains($content, 'GREEN')">3</xsl:when>
									<xsl:when test="contains($content, 'BLUE')">4</xsl:when>
									<xsl:when test="contains($content, 'CYAN') or contains($content, 'TEAL')">5</xsl:when>
									<xsl:when test="contains($content, 'YELLOW')">6</xsl:when>
									<xsl:when test="contains($content, 'PINK')">7</xsl:when>
									<xsl:when test="contains($content, 'BROWN') or contains($content, 'ORANGE')">8</xsl:when>
									<xsl:when test="contains($content, 'MAGENTA')">9</xsl:when>
									<xsl:otherwise/>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<!-- print the image or call a user defined template to print the cell -->
					<xsl:variable name="linkOnCell">
						<xsl:call-template name="linkOnCell">
							<xsl:with-param name="x" select="$xArg" />
							<xsl:with-param name="xArgIdx" select="$xArgIdx" />
							<xsl:with-param name="y" select="$yArg" />
							<xsl:with-param name="yArgIdx" select="$yArgIdx" />
							<xsl:with-param name="content" select="$content" />
							<xsl:with-param name="contentArgIdx" select="$contentArgIdx" />
							<xsl:with-param name="piece" select="$piece" />
							<xsl:with-param name="BoardName" select="$BoardName" />
						</xsl:call-template>
					</xsl:variable>
					
					<xsl:choose>
						<xsl:when test="$linkOnCell != ''">
							<a>
								<xsl:attribute name="style">
								 	min-width: <xsl:value-of select="$CellWidth - 2 * $BorderWidth"/>px;
								 	min-height: <xsl:value-of select="$CellHeight - 2 * $BorderWidth"/>px;
								</xsl:attribute>
								<xsl:attribute name="href"><xsl:value-of select="$linkOnCell"/></xsl:attribute>
								<xsl:call-template name="make_cell_content_or_chess_img">
									<xsl:with-param name="DefaultCellContent" select="$DefaultCellContent"/>
									<xsl:with-param name="xArg" select="$xArg"/>
									<xsl:with-param name="yArg" select="$yArg"/>
									<xsl:with-param name="content" select="$content"/>
									<xsl:with-param name="piece" select="$piece"/>
									<xsl:with-param name="CellColor" select="$CellColor"/>
									<xsl:with-param name="alt" select="$alt"/>
									<xsl:with-param name="CellWidth" select="$CellWidth"/>
									<xsl:with-param name="CellHeight" select="$CellHeight"/>
									<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
									<xsl:with-param name="xPosCell" select="$xPosCell"/>
									<xsl:with-param name="yPosCell" select="$yPosCell"/>
									<xsl:with-param name="BoardName" select="$BoardName"/>
								</xsl:call-template>
							</a>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="make_cell_content_or_chess_img">
								<xsl:with-param name="DefaultCellContent" select="$DefaultCellContent"/>
								<xsl:with-param name="xArg" select="$xArg"/>
								<xsl:with-param name="yArg" select="$yArg"/>
								<xsl:with-param name="content" select="$content"/>
								<xsl:with-param name="piece" select="$piece"/>
								<xsl:with-param name="CellColor" select="$CellColor"/>
								<xsl:with-param name="alt" select="$alt"/>
								<xsl:with-param name="CellWidth" select="$CellWidth"/>
								<xsl:with-param name="CellHeight" select="$CellHeight"/>
								<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
								<xsl:with-param name="xPosCell" select="$xPosCell"/>
								<xsl:with-param name="yPosCell" select="$yPosCell"/>
								<xsl:with-param name="BoardName" select="$BoardName"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				
			</xsl:for-each>
			<!-- print a caption with the name of the board, if defined -->
			<xsl:if test="$BoardName!=''">
				<p style="text-align: center">Board <xsl:value-of select="$BoardName"/></p>
			</xsl:if>
			
			<xsl:call-template name="selectedMove">
				<xsl:with-param name="xArgIdx" select="$xArgIdx" />
				<xsl:with-param name="yArgIdx" select="$yArgIdx" />
				<xsl:with-param name="BoardName" select="$BoardName" />

				<xsl:with-param name="internalMinX" select="$internalMinX"/>
				<xsl:with-param name="internalMinY" select="$internalMinY"/>
				<xsl:with-param name="mirrorY" select="$mirrorY"/>
				<xsl:with-param name="CellWidth" select="$CellWidth"/>
				<xsl:with-param name="CellHeight" select="$CellHeight"/>
				<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
				<xsl:with-param name="internalHeight" select="$internalHeight"/>
			</xsl:call-template>

			<xsl:call-template name="legalMoveLinks">
				<xsl:with-param name="xArgIdx" select="$xArgIdx" />
				<xsl:with-param name="yArgIdx" select="$yArgIdx" />
				<xsl:with-param name="BoardName" select="$BoardName" />

				<xsl:with-param name="internalMinX" select="$internalMinX"/>
				<xsl:with-param name="internalMinY" select="$internalMinY"/>
				<xsl:with-param name="mirrorY" select="$mirrorY"/>
				<xsl:with-param name="CellWidth" select="$CellWidth"/>
				<xsl:with-param name="CellHeight" select="$CellHeight"/>
				<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
				<xsl:with-param name="internalHeight" select="$internalHeight"/>
			</xsl:call-template>
		</div>
	</xsl:template>
	
	
	<xsl:template name="make_cell_content_or_chess_img">
		<xsl:param name="DefaultCellContent" />
		<xsl:param name="xArg" />
		<xsl:param name="yArg" />
		<xsl:param name="content" />
		<xsl:param name="piece" />
		<xsl:param name="CellColor" />
		<xsl:param name="alt" />
		<xsl:param name="CellWidth" />
		<xsl:param name="CellHeight" />
		<xsl:param name="BorderWidth" />
		<xsl:param name="xPosCell" />
		<xsl:param name="yPosCell" />
		<xsl:param name="BoardName" />
		
		<div class="chesscellcontent">
			<xsl:attribute name="id">chess_board_cell_<xsl:value-of select="$BoardName"/>_<xsl:value-of select="$xArg"/>_<xsl:value-of select="$yArg"/></xsl:attribute>
			<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
			<xsl:attribute name="style">
				left: <xsl:value-of select="$xPosCell"/>px;
				top: <xsl:value-of select="$yPosCell"/>px;
			</xsl:attribute>
			
			<xsl:choose>
				<xsl:when test="$DefaultCellContent!='yes' or contains($piece, 'UNKNOWN')">
					<xsl:call-template name="make_cell_content">
						<xsl:with-param name="xArg" select="$xArg"/>
						<xsl:with-param name="yArg" select="$yArg"/>
						<xsl:with-param name="content" select="$content"/>
						<xsl:with-param name="piece" select="$piece"/>
						<xsl:with-param name="background" select="$CellColor"/>
						<xsl:with-param name="width" select="$CellWidth - 2 * $BorderWidth"/>
						<xsl:with-param name="height" select="$CellHeight - 2 * $BorderWidth"/>
						<xsl:with-param name="alt" select="$alt"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$piece=''"></xsl:when><!-- empty cell -->
				<xsl:when test="$piece='MULTIPLE'"><b>?</b></xsl:when> <!-- multiple elements in cell -->
				<xsl:otherwise>
					<xsl:call-template name="make_chess_img">
						<xsl:with-param name="piece" select="$piece"/>
						<xsl:with-param name="background" select="$CellColor"/>
						<xsl:with-param name="imgWidth" select="$CellWidth - 2 * $BorderWidth"/>
						<xsl:with-param name="imgHeight" select="$CellHeight - 2 * $BorderWidth"/>
						<xsl:with-param name="alt" select="$alt"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		
		</div>
		
	</xsl:template>
	
	
	<!-- computes a numeric coordinate from the string representation -->
	<xsl:template name="coord2number">
		<xsl:param name="coord"/>
		<xsl:choose>
			<xsl:when test="string-length($coord)>1">
				<xsl:value-of select="number(translate($coord, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', ''))"/>
			</xsl:when>
			<xsl:when test="$coord='A'">1</xsl:when>
			<xsl:when test="$coord='B'">2</xsl:when>
			<xsl:when test="$coord='C'">3</xsl:when>
			<xsl:when test="$coord='D'">4</xsl:when>
			<xsl:when test="$coord='E'">5</xsl:when>
			<xsl:when test="$coord='F'">6</xsl:when>
			<xsl:when test="$coord='G'">7</xsl:when>
			<xsl:when test="$coord='H'">8</xsl:when>
			<xsl:when test="$coord='I'">9</xsl:when>
			<xsl:when test="$coord='J'">10</xsl:when>
			<xsl:when test="$coord='K'">11</xsl:when>
			<xsl:when test="$coord='L'">12</xsl:when>
			<xsl:when test="$coord='M'">13</xsl:when>
			<xsl:when test="$coord='N'">14</xsl:when>
			<xsl:when test="$coord='O'">15</xsl:when>
			<xsl:when test="$coord='P'">16</xsl:when>
			<xsl:when test="$coord='Q'">17</xsl:when>
			<xsl:when test="$coord='R'">18</xsl:when>
			<xsl:when test="$coord='S'">19</xsl:when>
			<xsl:when test="$coord='T'">20</xsl:when>
			<xsl:when test="$coord='U'">21</xsl:when>
			<xsl:when test="$coord='V'">22</xsl:when>
			<xsl:when test="$coord='W'">23</xsl:when>
			<xsl:when test="$coord='X'">24</xsl:when>
			<xsl:when test="$coord='Y'">25</xsl:when>
			<xsl:when test="$coord='Z'">26</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="number($coord)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- tries to detect the dimension of a board -->
	<xsl:template name="getDimension">
		<xsl:param name="cellFluentName"/>
		<xsl:param name="coordArgIdx"/>
		<xsl:param name="otherArgIdx"/>
		<xsl:param name="MinCoord"/>
		
		<xsl:variable name="MaxCoordVar">
			<xsl:call-template name="getMaxCoord">
				<xsl:with-param name="cellFluentName" select="$cellFluentName"/>
				<xsl:with-param name="coordArgIdx" select="$coordArgIdx"/>
				<xsl:with-param name="otherArgIdx" select="$otherArgIdx"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="MinCoordVar">
			<xsl:choose>
				<xsl:when test="$MinCoord!='?'"><xsl:value-of select="$MinCoord"/></xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getMinCoord">
						<xsl:with-param name="cellFluentName" select="$cellFluentName"/>
						<xsl:with-param name="coordArgIdx" select="$coordArgIdx"/>
						<xsl:with-param name="otherArgIdx" select="$otherArgIdx"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$MaxCoordVar - $MinCoordVar + 1"/>
	</xsl:template>
	
	<xsl:template name="getMinCoord">
		<xsl:param name="cellFluentName"/>
		<xsl:param name="coordArgIdx"/>
		<xsl:param name="otherArgIdx"/>
		
		<xsl:for-each select="fact[prop-f=$cellFluentName]/arg[number($coordArgIdx)]">
			<xsl:sort order="ascending" select="."/>
			<xsl:if test="position() = 1">
				<xsl:call-template name="coord2number">
					<xsl:with-param name="coord" select="."/>
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="getMaxCoord">
		<xsl:param name="cellFluentName"/>
		<xsl:param name="coordArgIdx"/>
		<xsl:param name="otherArgIdx"/>
		
		<xsl:for-each select="fact[prop-f=$cellFluentName]/arg[number($coordArgIdx)]">
			<xsl:sort order="descending" select="."/>
			<xsl:if test="position() = 1">
				<xsl:call-template name="coord2number">
					<xsl:with-param name="coord" select="."/>
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<!--
		make_chess_img prints the picture of a chess piece.
		valid pieces are:
			[abcdefghkmnpqrsz][dl23456789] - (piecename+color)
			O[0..9]                        - circles in different colors
			x[1..9]                        - numbers 1 to 9
			x[owx]                         - small black and white circles and x
			''                             - empty square
			[jD][01]                       - single and double checkers pieces in black and white
		background is either 'light' or 'dark'
	-->
	<xsl:template name="make_chess_img">
		<xsl:param name="piece"/>
		<xsl:param name="background">light</xsl:param>
		<xsl:param name="imgWidth">44</xsl:param>
		<xsl:param name="imgHeight">44</xsl:param>
		<xsl:param name="alt"/>
		<xsl:param name="style"/>
		
		<img>
			<xsl:attribute name="width"><xsl:value-of select="$imgWidth"/></xsl:attribute>
			<xsl:attribute name="height"><xsl:value-of select="$imgHeight"/></xsl:attribute>
			<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
			<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
			<xsl:if test="$style!=''">
				<xsl:attribute name="style"><xsl:value-of select="$style"/></xsl:attribute>
			</xsl:if>
			<xsl:attribute name="src">
				<xsl:value-of select="$stylesheetURL"/>
				<xsl:text>/generic/chess_images/Chess_</xsl:text>
				<xsl:value-of select="$piece"/>
				<xsl:value-of select="substring($background,1,1)"/>
				<xsl:text>44.png</xsl:text>
			</xsl:attribute>
		</img>
	</xsl:template>

	<!-- linkOnCell is called by print_chess_state for each cell of the board
	     and should return a url for the link or nothing if there should not be a link.
	     The default implementation returns nothing.
	 -->
	<xsl:template name="linkOnCell">
		<xsl:param name="x" />
		<xsl:param name="xArgIdx"/>
		<xsl:param name="y" />
		<xsl:param name="yArgIdx" />
		<xsl:param name="content" />
		<xsl:param name="contentArgIdx" />
		<xsl:param name="piece" />
		<xsl:param name="BoardName" />
	</xsl:template>

</xsl:stylesheet>
