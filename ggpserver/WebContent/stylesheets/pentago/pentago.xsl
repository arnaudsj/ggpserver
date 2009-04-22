<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Pentago
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/title.xsl"/>
	<xsl:import href="../generic/header.xsl"/>
	<xsl:import href="../generic/history.xsl"/>
	<xsl:import href="../generic/playerInfo.xsl"/>
	<xsl:import href="../generic/playClock.xsl"/>

	<xsl:template name="main" match="/">
		<html>
		
			<head>
				<xsl:call-template name="title"/>
			</head>	

			<body style="color: #111; background: #ffc;">

				<xsl:call-template name="header">	
					<xsl:with-param name="xPos">10px</xsl:with-param>
					<xsl:with-param name="yPos">10px</xsl:with-param>
				</xsl:call-template>	

				<xsl:call-template name="playClock">
					<xsl:with-param name="xPos">320px</xsl:with-param>
					<xsl:with-param name="yPos">110px</xsl:with-param>
				</xsl:call-template>				

				<xsl:call-template name="playerInfo">
					<xsl:with-param name="xPos">320px</xsl:with-param>
					<xsl:with-param name="yPos">160px</xsl:with-param>
				</xsl:call-template>

				<xsl:call-template name="history">
					<xsl:with-param name="xPos">320px</xsl:with-param>
					<xsl:with-param name="yPos">230px</xsl:with-param>
				</xsl:call-template>

				<style type="text/css" media="all">
					#main
					{
						position: absolute;
						left:     10px;
						top:      110px;
						width:    300px;
						height:   300px;
					}					
					#cell
					{
						width:  46px;
						height: 46px;
						float:	left;
						border: 2px solid #FFC;
						background-color: #CCCCCC;
					}
				</style>

				<!-- Draw Board -->
				<div id="main">
					<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>
					<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>
					<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>
					<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>
					<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>
					<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>
				</div>

				<!-- Draw Marks -->
				<xsl:for-each select="/match/state/fact[prop-f='CELLHOLDS']">

				    <xsl:variable name="quad" select="./arg[1]"/>
					<xsl:variable name="x"    select="50 * (./arg[2]-1) + 15"/>
					<xsl:variable name="y"    select="50 * (3-./arg[3]) + 115"/>

                    
					<xsl:if test="$quad='1'">
						<div>
							<xsl:attribute name="style">
								<xsl:value-of select="concat('position:absolute; left:', $x+150 ,'px; top:', $y ,'px; width:50px; height:50px;')"/>
							</xsl:attribute>		
							<xsl:if test="./arg[4]='RED'">
								<img src="../stylesheets/pentago/rc.gif"/>
							</xsl:if>
							<xsl:if test="./arg[4]='BLACK'">
								<img src="../stylesheets/pentago/bcc.gif"/>
							</xsl:if>						
						</div>				
					</xsl:if>	
					<xsl:if test="$quad='2'">
						<div>
							<xsl:attribute name="style">
								<xsl:value-of select="concat('position:absolute; left:', $x ,'px; top:', $y ,'px; width:50px; height:50px;')"/>
							</xsl:attribute>		
							<xsl:if test="./arg[4]='RED'">
								<img src="../stylesheets/pentago/rc.gif"/>
							</xsl:if>
							<xsl:if test="./arg[4]='BLACK'">
								<img src="../stylesheets/pentago/bcc.gif"/>
							</xsl:if>						
						</div>				
					</xsl:if>	
					<xsl:if test="$quad='3'">
						<div>
							<xsl:attribute name="style">
								<xsl:value-of select="concat('position:absolute; left:', $x ,'px; top:', $y+150 ,'px; width:50px; height:50px;')"/>
							</xsl:attribute>		
							<xsl:if test="./arg[4]='RED'">
								<img src="../stylesheets/pentago/rc.gif"/>
							</xsl:if>
							<xsl:if test="./arg[4]='BLACK'">
								<img src="../stylesheets/pentago/bcc.gif"/>
							</xsl:if>						
						</div>				
					</xsl:if>	
					<xsl:if test="$quad='4'">
						<div>
							<xsl:attribute name="style">
								<xsl:value-of select="concat('position:absolute; left:', $x+150 ,'px; top:', $y+150 ,'px; width:50px; height:50px;')"/>
							</xsl:attribute>		
							<xsl:if test="./arg[4]='RED'">
								<img src="../stylesheets/pentago/rc.gif"/>
							</xsl:if>
							<xsl:if test="./arg[4]='BLACK'">
								<img src="../stylesheets/pentago/bcc.gif"/>
							</xsl:if>						
						</div>				
					</xsl:if>	
				</xsl:for-each>
				
			</body>
		
		</html>
	</xsl:template>
</xsl:stylesheet>