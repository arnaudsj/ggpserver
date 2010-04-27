<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- 
	     This template implements some useful defaults for playing games:
	     	- if there is a binary move term (e.g., (MARK 1 3)) with first argument $x and second argument $y the move is selected by clicking on the link
	     	- if there is a move term with 4 or 5 arguments (e.g., (MOVE WP C 2 C 4)) the first click selects the source and the second the destination cell
	-->
	
	<xsl:template name="legalMoveLinks">
		<xsl:param name="xArgIdx"/>
		<xsl:param name="yArgIdx" />
		<xsl:param name="BoardName" />
		<xsl:param name="internalMinX"/>
		<xsl:param name="internalMinY"/>
		<xsl:param name="mirrorY"/>
		<xsl:param name="CellWidth"/>
		<xsl:param name="CellHeight"/>
		<xsl:param name="BorderWidth"/>
		<xsl:param name="internalHeight"/>
	
		<!-- add legal moves to JavaScript data structures -->
		<xsl:for-each select="/match/legalmoves/move[contains(move-term/prop-f, $BoardName)]">
			<xsl:choose>
				<!-- (MARK X Y) -->
				<xsl:when test="count(move-term/arg)=2">
					<xsl:variable name="x" select="move-term/arg[number($xArgIdx)]"/>
					<xsl:variable name="y" select="move-term/arg[number($yArgIdx)]"/>
					<script language="JavaScript" type="text/javascript">
						addBinaryMove("<xsl:value-of select="$BoardName"/>", "<xsl:value-of select="$x"/>", "<xsl:value-of select="$y"/>", 
							"<xsl:call-template name="makePlayLinkURL">
								<xsl:with-param name="chosenMove" select="move-number"/>
							</xsl:call-template>");
					</script>
					<xsl:variable name="moveString"><xsl:for-each select="move-term"><xsl:call-template name="move2text"/></xsl:for-each></xsl:variable>
					<xsl:call-template name="makeOverlayOnCell">
						<xsl:with-param name="xArg" select="$x" />
						<xsl:with-param name="yArg" select="$y" />
						<xsl:with-param name="url">javascript:selectCell("<xsl:value-of select="$BoardName"/>", "<xsl:value-of select="$x"/>", "<xsl:value-of select="$y"/>")</xsl:with-param>
						<xsl:with-param name="BoardName" select="$BoardName"/>
						<xsl:with-param name="type" select="'MARK'"/>
						<xsl:with-param name="title" select="$moveString"/>
						<xsl:with-param name="internalMinX" select="$internalMinX"/>
						<xsl:with-param name="internalMinY" select="$internalMinY"/>
						<xsl:with-param name="mirrorY" select="$mirrorY"/>
						<xsl:with-param name="CellWidth" select="$CellWidth"/>
						<xsl:with-param name="CellHeight" select="$CellHeight"/>
						<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
						<xsl:with-param name="internalHeight" select="$internalHeight"/>
					</xsl:call-template>
				</xsl:when>
				<!-- (MOVE X1 Y1 X2 Y2) or (MOVE PIECE X1 Y1 X2 Y2) -->
				<xsl:when test="count(move-term/arg)=4 or count(move-term/arg)=5">
					<xsl:variable name="offset" select="count(move-term/arg) - 4"/>
					<xsl:variable name="x1" select="move-term/arg[number($xArgIdx)+$offset]"/>
					<xsl:variable name="y1" select="move-term/arg[number($yArgIdx)+$offset]"/>
					<xsl:variable name="x2" select="move-term/arg[number($xArgIdx)+$offset+2]"/>
					<xsl:variable name="y2" select="move-term/arg[number($yArgIdx)+$offset+2]"/>
					<xsl:comment> --- <xsl:value-of select="move-term" /> --- <xsl:value-of select="$offset" /> --- <xsl:value-of select="$xArgIdx" />--- <xsl:value-of select="$yArgIdx" /> --- </xsl:comment>
					<script language="JavaScript" type="text/javascript">
						addQuaternaryMove("<xsl:value-of select="$BoardName"/>", "<xsl:value-of select="$x1"/>", "<xsl:value-of select="$y1"/>", "<xsl:value-of select="$x2"/>", "<xsl:value-of select="$y2"/>",
							"<xsl:call-template name="makePlayLinkURL">
								<xsl:with-param name="chosenMove" select="move-number"/>
							</xsl:call-template>");
					</script>
					<xsl:call-template name="makeOverlayOnCell">
						<xsl:with-param name="xArg" select="$x1" />
						<xsl:with-param name="yArg" select="$y1" />
						<xsl:with-param name="url">javascript:selectCell("<xsl:value-of select="$BoardName"/>", "<xsl:value-of select="$x1"/>", "<xsl:value-of select="$y1"/>")</xsl:with-param>
						<xsl:with-param name="BoardName" select="$BoardName"/>
						<xsl:with-param name="type" select="'SOURCE'"/>
						<xsl:with-param name="title" select="concat('move from ', $x1, ',', $y1)"/>
						<xsl:with-param name="internalMinX" select="$internalMinX"/>
						<xsl:with-param name="internalMinY" select="$internalMinY"/>
						<xsl:with-param name="mirrorY" select="$mirrorY"/>
						<xsl:with-param name="CellWidth" select="$CellWidth"/>
						<xsl:with-param name="CellHeight" select="$CellHeight"/>
						<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
						<xsl:with-param name="internalHeight" select="$internalHeight"/>
					</xsl:call-template>
					<xsl:call-template name="makeOverlayOnCell">
						<xsl:with-param name="xArg" select="$x2" />
						<xsl:with-param name="yArg" select="$y2" />
						<xsl:with-param name="url">javascript:selectCell("<xsl:value-of select="$BoardName"/>", "<xsl:value-of select="$x2"/>", "<xsl:value-of select="$y2"/>")</xsl:with-param>
						<xsl:with-param name="BoardName" select="$BoardName"/>
						<xsl:with-param name="type" select="'DEST'"/>
						<xsl:with-param name="title" select="concat('move to ', $x2, ',', $y2)"/>
						<xsl:with-param name="internalMinX" select="$internalMinX"/>
						<xsl:with-param name="internalMinY" select="$internalMinY"/>
						<xsl:with-param name="mirrorY" select="$mirrorY"/>
						<xsl:with-param name="CellWidth" select="$CellWidth"/>
						<xsl:with-param name="CellHeight" select="$CellHeight"/>
						<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
						<xsl:with-param name="internalHeight" select="$internalHeight"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>

	</xsl:template>

	<xsl:template name="selectedMove">
		<xsl:param name="xArgIdx"/>
		<xsl:param name="yArgIdx" />
		<xsl:param name="BoardName" />
		<xsl:param name="internalMinX"/>
		<xsl:param name="internalMinY"/>
		<xsl:param name="mirrorY"/>
		<xsl:param name="CellWidth"/>
		<xsl:param name="CellHeight"/>
		<xsl:param name="BorderWidth"/>
		<xsl:param name="internalHeight"/>
	
		<xsl:for-each select="/match/chosenmove[contains(move-term/prop-f, $BoardName)]">
			<xsl:variable name="moveString"><xsl:for-each select="move-term"><xsl:call-template name="move2text"/></xsl:for-each></xsl:variable>
			<xsl:choose>
				<!-- (MARK X Y) -->
				<xsl:when test="count(move-term/arg)=2">
					<xsl:variable name="x" select="move-term/arg[number($xArgIdx)]"/>
					<xsl:variable name="y" select="move-term/arg[number($yArgIdx)]"/>
					<xsl:call-template name="makeOverlayOnCell">
						<xsl:with-param name="xArg" select="$x" />
						<xsl:with-param name="yArg" select="$y" />
						<xsl:with-param name="BoardName" select="$BoardName"/>
						<xsl:with-param name="type" select="'CHOSENMOVE'"/>
						<xsl:with-param name="title" select="$moveString"/>
						<xsl:with-param name="internalMinX" select="$internalMinX"/>
						<xsl:with-param name="internalMinY" select="$internalMinY"/>
						<xsl:with-param name="mirrorY" select="$mirrorY"/>
						<xsl:with-param name="CellWidth" select="$CellWidth"/>
						<xsl:with-param name="CellHeight" select="$CellHeight"/>
						<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
						<xsl:with-param name="internalHeight" select="$internalHeight"/>
					</xsl:call-template>
				</xsl:when>
				<!-- (MOVE X1 Y1 X2 Y2) or (MOVE PIECE X1 Y1 X2 Y2) -->
				<xsl:when test="count(move-term/arg)=4 or count(move-term/arg)=5">
					<xsl:variable name="offset" select="count(move-term/arg) - 4"/>
					<xsl:variable name="x1" select="move-term/arg[number($xArgIdx)+$offset]"/>
					<xsl:variable name="y1" select="move-term/arg[number($yArgIdx)+$offset]"/>
					<xsl:variable name="x2" select="move-term/arg[number($xArgIdx)+$offset+2]"/>
					<xsl:variable name="y2" select="move-term/arg[number($yArgIdx)+$offset+2]"/>
					<xsl:call-template name="makeOverlayOnCell">
						<xsl:with-param name="xArg" select="$x1" />
						<xsl:with-param name="yArg" select="$y1" />
						<xsl:with-param name="BoardName" select="$BoardName"/>
						<xsl:with-param name="type" select="'CHOSENMOVE'"/>
						<xsl:with-param name="title" select="$moveString"/>
						<xsl:with-param name="internalMinX" select="$internalMinX"/>
						<xsl:with-param name="internalMinY" select="$internalMinY"/>
						<xsl:with-param name="mirrorY" select="$mirrorY"/>
						<xsl:with-param name="CellWidth" select="$CellWidth"/>
						<xsl:with-param name="CellHeight" select="$CellHeight"/>
						<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
						<xsl:with-param name="internalHeight" select="$internalHeight"/>
					</xsl:call-template>
					<xsl:call-template name="makeOverlayOnCell">
						<xsl:with-param name="xArg" select="$x2" />
						<xsl:with-param name="yArg" select="$y2" />
						<xsl:with-param name="BoardName" select="$BoardName"/>
						<xsl:with-param name="type" select="'CHOSENMOVE'"/>
						<xsl:with-param name="title" select="$moveString"/>
						<xsl:with-param name="internalMinX" select="$internalMinX"/>
						<xsl:with-param name="internalMinY" select="$internalMinY"/>
						<xsl:with-param name="mirrorY" select="$mirrorY"/>
						<xsl:with-param name="CellWidth" select="$CellWidth"/>
						<xsl:with-param name="CellHeight" select="$CellHeight"/>
						<xsl:with-param name="BorderWidth" select="$BorderWidth"/>
						<xsl:with-param name="internalHeight" select="$internalHeight"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>

	</xsl:template>

	<xsl:template name="makeOverlayOnCell">
		<xsl:param name="xArg"/>
		<xsl:param name="yArg"/>
		<xsl:param name="url" select="''"/>
		<xsl:param name="BoardName"/>
		<xsl:param name="type"/>
		<xsl:param name="title"/>

		<xsl:param name="internalMinX"/>
		<xsl:param name="internalMinY"/>
		<xsl:param name="mirrorY"/>
		<xsl:param name="CellWidth"/>
		<xsl:param name="CellHeight"/>
		<xsl:param name="BorderWidth"/>
		<xsl:param name="internalHeight"/>
		
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
		
		<xsl:variable name="color">
			<xsl:choose>
				<xsl:when test="$type='MARK' or $type='DEST'">green</xsl:when>
				<xsl:when test="$type='SOURCE'">red</xsl:when>
				<xsl:when test="$type='CHOSENMOVE'">white</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="opacity">
			<xsl:choose>
				<xsl:when test="$type='MARK' or $type='SOURCE'">0.3</xsl:when>
				<xsl:when test="$type='DEST'">0.0</xsl:when>
				<xsl:when test="$type='CHOSENMOVE'">0.6</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<div class="chesscellcontent">
			<xsl:attribute name="id">chess_cell_<xsl:choose><xsl:when test="$type='CHOSENMOVE'">chosenmove</xsl:when><xsl:otherwise>link</xsl:otherwise></xsl:choose>_<xsl:value-of select="$BoardName"/>_<xsl:value-of select="$xArg"/>_<xsl:value-of select="$yArg"/></xsl:attribute>
			<xsl:attribute name="style">
				left: <xsl:value-of select="$xPosCell"/>px;
				top: <xsl:value-of select="$yPosCell"/>px;
			 	background-color: <xsl:value-of select="$color"/>;
			 	opacity: <xsl:value-of select="$opacity"/>;
			</xsl:attribute>
				<!--  position: absolute;
			 	width: <xsl:value-of select="$CellWidth - 2 * $BorderWidth"/>px;
			 	height: <xsl:value-of select="$CellHeight - 2 * $BorderWidth"/>px;
			 	-->
			<xsl:if test="$url!=''">
				<a>
					<xsl:attribute name="style">
						display: block;
					 	width: <xsl:value-of select="$CellWidth - 2 * $BorderWidth"/>px;
					 	height: <xsl:value-of select="$CellHeight - 2 * $BorderWidth"/>px;
					</xsl:attribute>
					<xsl:attribute name="title"><xsl:value-of select="$title"/></xsl:attribute>
					<xsl:attribute name="href"><xsl:value-of select="$url"/></xsl:attribute>
				</a>
			</xsl:if>
		</div>		

	</xsl:template>

	<xsl:template name="selectCellJavaScript">
		<xsl:param name="xArgIdx" />
		<xsl:param name="yArgIdx" />
		
		<script language="JavaScript" type="text/javascript">
			&lt;!--
			<![CDATA[
				/*
					board .. board name or '' (if there is just one)
					x, y .. x and y coordinates (names) of the cell
					highlight .. true or false
				*/
				function highlightChessBoardCell(board, x, y, highlight) {
					if (highlight) {
						document.getElementById("chess_cell_link_"+board+"_"+x+"_"+y).style.opacity = 0.3;
					} else {
						document.getElementById("chess_cell_link_"+board+"_"+x+"_"+y).style.opacity = 0.0;
					}
				};
			]]>
			--&gt;
		</script>

		<xsl:if test="count(/match/legalmoves/move)>0 and 3>number($xArgIdx) and 3>number($yArgIdx)">
			<script language="JavaScript" type="text/javascript">
				<![CDATA[
					var selectedX = "", selectedY = "", selectedBoard = "";
					var binaryMove = new Array();
					var quaternaryMove = new Array();
					
					function addBinaryMove(board, x, y, url) {
						if (binaryMove[board] == undefined) {
							binaryMove[board] = new Array();
						}
						if (binaryMove[board][x] == undefined) {
							binaryMove[board][x] = new Array();
						}
						if (binaryMove[board][x][y] == undefined) {
							binaryMove[board][x][y] = url;
						} else {
							// alert("move already defined ("+board+","+x+","+y+","+url+")");
						}
					};

					function addQuaternaryMove(board, x1, y1, x2, y2, url) {
						if (quaternaryMove[board] == undefined) {
							quaternaryMove[board] = new Array();
						}
						if (quaternaryMove[board][x1] == undefined) {
							quaternaryMove[board][x1] = new Array();
						}
						if (quaternaryMove[board][x1][y1] == undefined) {
							quaternaryMove[board][x1][y1] = new Array();
						}
						if (quaternaryMove[board][x1][y1] == undefined) {
							quaternaryMove[board][x1][y1] = new Array();
						}
						if (quaternaryMove[board][x1][y1][x2] == undefined) {
							quaternaryMove[board][x1][y1][x2] = new Array();
						}
						if (quaternaryMove[board][x1][y1][x2][y2] == undefined) {
							quaternaryMove[board][x1][y1][x2][y2] = url;
						} else {
							// alert("move already defined ("+board+","+x1+","+y1+","+x2+","+y2+","+url+")");
						}
					};

					function selectCell(board, x, y) {
						if (binaryMove[board] != undefined && binaryMove[board][x] != undefined && binaryMove[board][x][y] != undefined) {
							location.replace(binaryMove[board][x][y]);
						} else if (selectedX == "") {
							selectedX = x;
							selectedY = y;
							selectedBoard = board;
							for (destX in quaternaryMove[board][x][y]) {
								for (destY in quaternaryMove[board][x][y][destX]) {
									highlightChessBoardCell(board, destX, destY, true);
								}
							}
						} else if (
							board == selectedBoard &&
							quaternaryMove[board] != undefined &&
							quaternaryMove[board][selectedX] != undefined &&
							quaternaryMove[board][selectedX][selectedY] != undefined &&
							quaternaryMove[board][selectedX][selectedY][x] != undefined &&
							quaternaryMove[board][selectedX][selectedY][x][y] != undefined) {
								location.replace(quaternaryMove[board][selectedX][selectedY][x][y]);
						} else {
							// unhighlight previously highlighted cells
							for (destX in quaternaryMove[selectedBoard][selectedX][selectedY]) {
								for (destY in quaternaryMove[selectedBoard][selectedX][selectedY][destX]) {
									highlightChessBoardCell(selectedBoard, destX, destY, false);
								}
							}
							// reset selectedX, selectedY
							selectedX = ""; selectedY = ""; selectedBoard = "";
							// call selectCell again
							selectCell(board, x, y);
						}
					};
				]]>
			</script>
		</xsl:if>
	</xsl:template>
		
</xsl:stylesheet>