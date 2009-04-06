<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing play-clock information.
	- Assumes data will be found in
		<match><startclock>...
		<match><playclock>...
		<match><timestamp>...
	- For use within <body>.
	- Accepts args: xPos and yPos which specify absolute position.	
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="playClock">

		<xsl:param name="xPos"/>
		<xsl:param name="yPos"/>

		<script language="JavaScript" type="text/javascript">
			<![CDATA[
				<!--		
					var sec_;
					var display_;
			
					function turnOffTimer()
					{
						document.getElementById("timer").innerHTML="Inactive (Game Over)";
					}
			
					function clock(sec, starttime)
					{
						var now=new Date();
						var seconds_left=Math.round(sec+(starttime-now.getTime())/1000);
						if(seconds_left>0){
							sec_=seconds_left;
							loop();
						}else{
							document.getElementById("timer").innerHTML="Inactive (Not the current step)";
						}
					}

					function loop()
					{
						sec_--;
						document.getElementById("timer").innerHTML=sec_;
						
						if ( (sec_<=0) )
						{
							setTimeout("shownextstep()", 2000);
						}
						else
						{
							setTimeout("loop()", 1000);
						}
					}
					function shownextstep()
					{
						document.location.replace(nextState);
					}
				// -->
			]]>
		</script>

		<xsl:variable name="currentStep" select="count(/match/history/step)+1"/>
			<script language="JavaScript" type="text/javascript">
				<xsl:text disable-output-escaping="yes">nextState="step_</xsl:text>
				<xsl:value-of select="$currentStep+1"/>
				<xsl:text disable-output-escaping="yes">.xml";</xsl:text>
			</script>

		<!-- Case: history but no reward was found ie. we're in the middle of a game -->
		<xsl:if test="not(/match/scores/reward) and /match/history/step">
			<script language="JavaScript" type="text/javascript">
				<xsl:text disable-output-escaping="yes">document.body.setAttribute('onLoad', 'clock( </xsl:text>
				<xsl:value-of select="/match/playclock"/>
				<xsl:text disable-output-escaping="yes">, </xsl:text>
				<xsl:value-of select="/match/timestamp"/>
				<xsl:text disable-output-escaping="yes">)');</xsl:text>
			</script>
		</xsl:if>

		<!-- Case: no history found ie. the game hasn't started) -->
		<xsl:if test="not(/match/history/step)">
			<script language="JavaScript" type="text/javascript">
				<xsl:text disable-output-escaping="yes">document.body.setAttribute('onLoad', 'clock( </xsl:text>
				<xsl:value-of select="/match/startclock"/>
				<xsl:text disable-output-escaping="yes">+</xsl:text>
				<xsl:value-of select="/match/playclock"/>
				<xsl:text disable-output-escaping="yes">, </xsl:text>
				<xsl:value-of select="/match/timestamp"/>
				<xsl:text disable-output-escaping="yes">)');</xsl:text>
			</script>
		</xsl:if>

		<!-- Case: a reward was found => the game is over -->
		<xsl:if test="/match/scores/reward">
			<script language="JavaScript" type="text/javascript">
				document.body.setAttribute('onLoad', 'turnOffTimer()');
			</script>
		</xsl:if>

		<style type="text/css" media="all">@import 
			url("../../styles/css/main.css");
		</style>
		
		<div id="playClock">

			<xsl:attribute name="style">
				position:absolute;
				left: <xsl:value-of select="$xPos"/>;
				top:  <xsl:value-of select="$yPos"/>;
			</xsl:attribute>

			<span class="heading">
				<xsl:choose>
					<xsl:when test="not(/match/history/step)">Start Clock:</xsl:when>
					<xsl:otherwise>Play Clock:</xsl:otherwise>
				</xsl:choose>
			</span>
			<div id="underline" style="width:150px"></div>		
		
			<table>
				<tr>
					<td> 
						<span class="heading">Remaining:</span>
					</td>
					<td>
						<span class="content" id="timer"></span>
					</td>
				</tr>
			</table>	

		</div>
			
	</xsl:template>
</xsl:stylesheet>