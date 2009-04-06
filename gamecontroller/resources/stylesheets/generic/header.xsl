<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing the generic game-master header.
	- For use within <body>.
	- Accepts args: xPos and yPos which specify absolute position.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="header">

		<xsl:param name="xPos"/>
		<xsl:param name="yPos"/>

		<style type="text/css" media="all">@import 
			url("css/main.css");
		</style>
		
		<div id="header">
		
			<xsl:attribute name="style">
				position:absolute;
				left: <xsl:value-of select="$xPos"/>;
				top:  <xsl:value-of select="$yPos"/>;
			</xsl:attribute>
			
<!--
			<div id="topbar">
 				<div id="logo">
					<img src="http://games.stanford.edu/gamemaster/images/ggp.gif"/>
				</div>
				<div class="bartab" id="home">
					<a href="/">Home</a>
				</div>
				<div class="bartab" id="spectator">
					<a href="/spectator/displayclass?class=match">Spectator</a>
				</div>
				<div class="bartab" id="player">
					<a href="/player/displayclass?class=playable">Player</a>
				</div>
				<div class="bartab" id="director">
					<a href="/director/displayclass?class=runnable">Director</a>
				</div>
				<div class="bartab" id="profile">
					<a href="/profiler/profile?">Profile</a>
				</div>
			</div>
-->
			<xsl:variable name="currentStep" select="count(/match/history/step)+1"/>

			<div id="topbar">
				<span class="heading">Match:</span><span class="content"><xsl:value-of select="/match/match-id"/></span>
				<br/><br/>
				<xsl:choose>
					<xsl:when test="not(/match/history/step)"><!-- this is the initial state => don't link to initial and previous state -->
						<div class="bartab">
							<img src="../../stylesheets/generic/images/leftarrow.png" width="20" height="20"/> Initial State
						</div>
						<div class="bartab">
							<img src="../../stylesheets/generic/images/leftarrow.png" width="20" height="20"/> Previous State
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="bartab">
							<a href="step_1.xml">
								<img src="../../stylesheets/generic/images/leftarrow.png" width="20" height="20"/> Initial State
							</a>
						</div>
						<div class="bartab">
							<a>
							<xsl:attribute name="href">
								step_<xsl:value-of select="$currentStep - 1"/>.xml
							</xsl:attribute>
								<img src="../../stylesheets/generic/images/leftarrow.png" width="20" height="20"/> Previous State
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>

				<xsl:choose>
					<xsl:when test="/match/scores/reward"><!-- this is the final state => don't link to next and final state -->
						<div class="bartab">
							<img src="../../stylesheets/generic/images/rightarrow.png" width="20" height="20"/> Next State
						</div>
						<div class="bartab">
							<img src="../../stylesheets/generic/images/rightarrow.png" width="20" height="20"/> Final State
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="bartab">
							<a>
							<xsl:attribute name="href">
								step_<xsl:value-of select="$currentStep + 1"/>.xml
							</xsl:attribute>
								<img src="../../stylesheets/generic/images/rightarrow.png" width="20" height="20"/> Next State
							</a>
						</div>
						<div class="bartab">
							<a href="finalstate.xml">
								<img src="../../stylesheets/generic/images/rightarrow.png" width="20" height="20"/> Final State
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<div id="underline"/>
		</div>	
	
	</xsl:template>
</xsl:stylesheet>