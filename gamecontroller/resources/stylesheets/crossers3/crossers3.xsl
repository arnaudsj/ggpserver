<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_crossers3_board"/>
		
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'POS'"/>
			<xsl:with-param name="excludeFluent2" select="'WALL'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="print_crossers3_board">
		<style type="text/css" media="all">
			div.spacerC1{
				width: 224px;
				height: 32px;
				clear: left;
				float: left;
			}
			div.spacerC2{
				width: 196px;
				height: 32px;
				clear: left;
				float: left;
			}
			div.spacerC3{
				width: 168px;
				height: 32px;
				clear: left;
				float: left;
			}
			div.spacerC4{
				width: 140px;
				height: 32px;
				clear: left;
				float: left;
			}
			div.spacerC5{
				width: 112px;
				height: 32px;
				clear: left;
				float: left;
			}
			div.spacerC6{
				width: 84px;
				height: 32px;
				clear: left;
				float: left;
			}
			div.spacerC7{
				width: 56px;
				height: 32px;
				clear: left;
				float: left;
			}
			div.spacerC8{
				width: 28px;
				height: 32px;
				clear: left;
				float: left;
			}
			div.spacerC9{
				width: 0px;
				height: 32px;
				clear: left;
				float: left;
			}
			div.spacerwallC1{
				width: 210px;
				height: 18px;
				clear: left;
				float: left;
			}
			div.spacerwallC2{
				width: 182px;
				height: 18px;
				clear: left;
				float: left;
			}
			div.spacerwallC3{
				width: 154px;
				height: 18px;
				clear: left;
				float: left;
			}
			div.spacerwallC4{
				width: 126px;
				height: 18px;
				clear: left;
				float: left;
			}
			div.spacerwallC5{
				width: 98px;
				height: 18px;
				clear: left;
				float: left;
			}
			div.spacerwallC6{
				width: 70px;
				height: 18px;
				clear: left;
				float: left;
			}
			div.spacerwallC7{
				width: 42px;
				height: 18px;
				clear: left;
				float: left;
			}
			div.spacerwallC8{
				width: 14px;
				height: 18px;
				clear: left;
				float: left;
			}
			div.cell{
				float: left;
				width: 32px;
				height: 32px;
				background: #cca083;
			}
			div.cell_LEFT{
				float: left;
				width: 32px;
				height: 32px;
				background: #ff0000;
			}
			div.cell_RIGHT{
				float: left;
				width: 32px;
				height: 32px;
				background: #0000ff;
			}
			div.cell_TOP{
				float: left;
				width: 32px;
				height: 32px;
				background: #00ff00;
			}
			div.cell_LEFT_RIGHT{
				float: left;
				width: 32px;
				height: 32px;
				background: #ff00ff;
			}
			div.cell_LEFT_TOP{
				float: left;
				width: 32px;
				height: 32px;
				background: #ffff00;
			}
			div.cell_RIGHT_TOP{
				float: left;
				width: 32px;
				height: 32px;
				background: #00ffff;
			}
			div.cell_LEFT_RIGHT_TOP{
				float: left;
				width: 32px;
				height: 32px;
				background: #ffffff;
			}
			
			div.wall_downleft{
				padding: 0px;
				margin: 0px;
				float: left;
				width: 28px;
				height: 14px;
			}
			div.wall_downright{
				padding: 0px;
				margin: 0px;
				float: left;
				width: 28px;
				height: 14px;
			}
			div.wall_horizontal{
				padding: 0px;
				margin: 0px;
				float: left;
				width: 24px;
				height: 32px;
			}
			div.crossers3_board{
				position: relative;
				width: 490px;
				height: 440px;
				padding: 10px;
				border: 2px solid #b17735;
				background: transparent url(<xsl:value-of select="$stylesheetURL"/>/crossers3/crossersboard.gif) repeat top left;
			}
		</style>
		<div class="crossers3_board">
			<xsl:call-template name="makerows">
				<xsl:with-param name="row" select="'C1'"/>
			</xsl:call-template>
		</div>
	
	</xsl:template>

	<xsl:template name="nextcoord">
		<xsl:param name="coord"/>
		<xsl:choose>
			<xsl:when test="$coord='C1'">C2</xsl:when>
			<xsl:when test="$coord='C2'">C3</xsl:when>
			<xsl:when test="$coord='C3'">C4</xsl:when>
			<xsl:when test="$coord='C4'">C5</xsl:when>
			<xsl:when test="$coord='C5'">C6</xsl:when>
			<xsl:when test="$coord='C6'">C7</xsl:when>
			<xsl:when test="$coord='C7'">C8</xsl:when>
			<xsl:when test="$coord='C8'">C9</xsl:when>
		</xsl:choose>
	</xsl:template>
		
	<xsl:template name="makerows">
		<xsl:param name="row"/>
		<div><xsl:attribute name="class">spacer<xsl:value-of select="$row"/></xsl:attribute></div>
		<xsl:call-template name="makecols">
			<xsl:with-param name="row" select="$row"></xsl:with-param>
			<xsl:with-param name="col" select="'C1'"></xsl:with-param>
		</xsl:call-template>
		<xsl:if test="$row!='C9'">
			<xsl:variable name="nextrow">
				<xsl:call-template name="nextcoord">
					<xsl:with-param name="coord" select="$row"/>
				</xsl:call-template>
			</xsl:variable>
			<div><xsl:attribute name="class">spacerwall<xsl:value-of select="$row"/></xsl:attribute></div>
			<xsl:call-template name="makewalls">
				<xsl:with-param name="row1" select="$row"/>
				<xsl:with-param name="row2" select="$nextrow"/>
				<xsl:with-param name="col" select="'C1'"/>
			</xsl:call-template>
			<xsl:call-template name="makerows">
				<xsl:with-param name="row" select="$nextrow"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="makewalls">
		<xsl:param name="row1"/>
		<xsl:param name="row2"/>
		<xsl:param name="col"/>
		<xsl:call-template name="makewall">
			<xsl:with-param name="row1" select="$row1"/>
			<xsl:with-param name="col1" select="$col"/>
			<xsl:with-param name="row2" select="$row2"/>
			<xsl:with-param name="col2" select="$col"/>
			<xsl:with-param name="type" select="'downleft'"/>
		</xsl:call-template>
		<xsl:variable name="nextcol">
			<xsl:call-template name="nextcoord">
				<xsl:with-param name="coord" select="$col"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="makewall">
			<xsl:with-param name="row1" select="$row1"/>
			<xsl:with-param name="col1" select="$col"/>
			<xsl:with-param name="row2" select="$row2"/>
			<xsl:with-param name="col2" select="$nextcol"/>
			<xsl:with-param name="type" select="'downright'"/>
		</xsl:call-template>
		<xsl:if test="$nextcol!=$row2">
			<xsl:call-template name="makewalls">
				<xsl:with-param name="row1" select="$row1"/>
				<xsl:with-param name="row2" select="$row2"/>
				<xsl:with-param name="col" select="$nextcol"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="makecols">
		<xsl:param name="row"/>
		<xsl:param name="col"/>
		<xsl:call-template name="makecell">
			<xsl:with-param name="row" select="$row"/>
			<xsl:with-param name="col" select="$col"/>
		</xsl:call-template>
		<xsl:if test="$col!=$row">
			<xsl:variable name="nextcol">
				<xsl:call-template name="nextcoord">
					<xsl:with-param name="coord" select="$col"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:call-template name="makewall">
				<xsl:with-param name="row1" select="$row"/>
				<xsl:with-param name="col1" select="$col"/>
				<xsl:with-param name="row2" select="$row"/>
				<xsl:with-param name="col2" select="$nextcol"/>
				<xsl:with-param name="type" select="'horizontal'"/>
			</xsl:call-template>
			<xsl:call-template name="makecols">
				<xsl:with-param name="row" select="$row"/>
				<xsl:with-param name="col" select="$nextcol"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="makewall">
		<xsl:param name="row1"/>
		<xsl:param name="col1"/>
		<xsl:param name="row2"/>
		<xsl:param name="col2"/>
		<xsl:param name="type"/>
		<div>
			<xsl:attribute name="class">wall_<xsl:value-of select="$type"/></xsl:attribute>
			<xsl:if test="fact[prop-f='WALL' and arg[1]=$col1 and arg[2]=$row1 and arg[3]=$col2 and arg[4]=$row2]">
				<img>
					<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>/crossers3/crosserswall_<xsl:value-of select="$type"/>.gif</xsl:attribute>
				</img>
			</xsl:if>
		</div>
	</xsl:template>
		
	<xsl:template name="makecell">
		<xsl:param name="row"/>
		<xsl:param name="col"/>
		<div>
			<xsl:attribute name="class">cell<xsl:for-each select="fact[prop-f='POS' and arg[2]=$col and arg[3]=$row]"><xsl:sort select="arg[1]"/>_<xsl:value-of select="arg[1]"/></xsl:for-each></xsl:attribute>
			<img>
				<xsl:variable name="pitimage">
					<xsl:choose>
						<xsl:when test="fact[prop-f='POS' and arg[2]=$col and arg[3]=$row]">crossersboardpit.gif</xsl:when>
						<xsl:otherwise>crossersboardpitb.gif</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>crossers3/<xsl:value-of select="$pitimage"/></xsl:attribute>
			</img>
		</div>
	</xsl:template>

</xsl:stylesheet>
