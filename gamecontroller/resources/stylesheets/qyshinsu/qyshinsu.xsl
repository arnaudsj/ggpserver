<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	<xsl:import href="../generic/sitespecific.xsl"/>

	<xsl:template name="print_state">
		<!-- Draw Board -->
		<div id="qyshinsu_board" style="position:relative; width:400px; height:400px">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="concat($stylesheetURL,'/qyshinsu/board.png')"/></xsl:attribute>
			</img>

			<xsl:for-each select="fact[prop-f='POSITION']">
				<xsl:if test="./arg[2]!='EMPTY'">
					<xsl:variable name="pos" select="./arg[1]"/>
					<xsl:variable name="owner" select="../fact[prop-f='OWNER' and arg[1]=$pos]/arg[2]"/>
					<xsl:variable name="value" select="./arg[2]"/>
					<xsl:variable name="alt">
						<xsl:value-of select="$owner"/>
						<xsl:text> piece </xsl:text>
						<xsl:value-of select="$value"/>
						<xsl:text> at position </xsl:text>
						<xsl:value-of select="$pos"/>
					</xsl:variable>

					<img>
						<xsl:attribute name="src">
							<xsl:if test="$owner='RED'"><xsl:value-of select="concat($stylesheetURL,'/qyshinsu/','R',$value,'.png')"/></xsl:if>
							<xsl:if test="$owner='BLACK'"><xsl:value-of select="concat($stylesheetURL,'/qyshinsu/','B',$value,'.png')"/></xsl:if>
						</xsl:attribute>
						<xsl:attribute name="style">
							position:absolute;
							<!-- TABLE OF POSITIONS -->
							<xsl:choose>
								<xsl:when test="$pos=1">
									<xsl:value-of select="'left: 255px; top: 70px'"/>
								</xsl:when>
								<xsl:when test="$pos=2">
									<xsl:value-of select="'left: 290px; top: 120px'"/>
								</xsl:when>
								<xsl:when test="$pos=3">
									<xsl:value-of select="'left: 298px; top: 185px'"/>
								</xsl:when>
								<xsl:when test="$pos=4">
									<xsl:value-of select="'left: 270px; top: 245px'"/>
								</xsl:when>
								<xsl:when test="$pos=5">
									<xsl:value-of select="'left: 218px; top: 285px'"/>
								</xsl:when>
								<xsl:when test="$pos=6">
									<xsl:value-of select="'left: 153px; top: 290px'"/>
								</xsl:when>
								<xsl:when test="$pos=7">
									<xsl:value-of select="'left: 95px; top: 265px'"/>
								</xsl:when>
								<xsl:when test="$pos=8">
									<xsl:value-of select="'left: 55px; top: 215px'"/>
								</xsl:when>
								<xsl:when test="$pos=9">
									<xsl:value-of select="'left: 45px; top: 145px'"/>
								</xsl:when>
								<xsl:when test="$pos=10">
									<xsl:value-of select="'left: 75px; top: 90px'"/>
								</xsl:when>
								<xsl:when test="$pos=11">
									<xsl:value-of select="'left: 130px; top: 50px'"/>
								</xsl:when>
								<xsl:when test="$pos=12">
									<xsl:value-of select="'left: 195px; top: 45px'"/>
								</xsl:when>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
						<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
					</img>

				</xsl:if>
			</xsl:for-each>
		
			<xsl:for-each select="fact[prop-f='LEGALPLAYLOC']">
				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				<xsl:variable name="pos" select="./arg[1]"/>
				<xsl:if test="
							../fact[prop-f='OWNER' and arg[1]=$pos]/arg[2] = ../fact[prop-f='CONTROL']/arg[1]
							or
							../fact[prop-f='POSITION' and arg[1]=$pos]/arg[2] = 'EMPTY'
							">
					<div>
						<xsl:attribute name="style">
							position:absolute;
							width: 10px;
							height: 10px;
							background-color: lightgreen;
							<!-- TABLE OF POSITIONS -->
							<xsl:choose>
								<xsl:when test="$pos=1">
									<xsl:value-of select="'left: 275px; top: 90px'"/>
								</xsl:when>
								<xsl:when test="$pos=2">
									<xsl:value-of select="'left: 310px; top: 140px'"/>
								</xsl:when>
								<xsl:when test="$pos=3">
									<xsl:value-of select="'left: 318px; top: 205px'"/>
								</xsl:when>
								<xsl:when test="$pos=4">
									<xsl:value-of select="'left: 290px; top: 265px'"/>
								</xsl:when>
								<xsl:when test="$pos=5">
									<xsl:value-of select="'left: 238px; top: 305px'"/>
								</xsl:when>
								<xsl:when test="$pos=6">
									<xsl:value-of select="'left: 173px; top: 310px'"/>
								</xsl:when>
								<xsl:when test="$pos=7">
									<xsl:value-of select="'left: 115px; top: 275px'"/>
								</xsl:when>
								<xsl:when test="$pos=8">
									<xsl:value-of select="'left: 75px; top: 235px'"/>
								</xsl:when>
								<xsl:when test="$pos=9">
									<xsl:value-of select="'left: 65px; top: 165px'"/>
								</xsl:when>
								<xsl:when test="$pos=10">
									<xsl:value-of select="'left: 95px; top: 110px'"/>
								</xsl:when>
								<xsl:when test="$pos=11">
									<xsl:value-of select="'left: 150px; top: 70px'"/>
								</xsl:when>
								<xsl:when test="$pos=12">
									<xsl:value-of select="'left: 215px; top: 65px'"/>
								</xsl:when>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
					</div>
				</xsl:if>
			</xsl:for-each>
		
		</div>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'POSITION'"/>
			<xsl:with-param name="excludeFluent2" select="'OWNER'"/>
			<xsl:with-param name="excludeFluent3" select="'LEGALPLAYLOC'"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
