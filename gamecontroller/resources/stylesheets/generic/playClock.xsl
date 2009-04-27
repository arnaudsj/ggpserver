<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing play-clock information.
	- Assumes data will be found in
		<match><startclock>...
		<match><playclock>...
		<match><timestamp>...
	- For use within <body>.
	- needs css/main.css
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="playClock">

		<xsl:variable name="currentStep" select="count(/match/history/step)+1"/>

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

			<xsl:text disable-output-escaping="yes">nextState="step_</xsl:text>
			<xsl:value-of select="$currentStep+1"/>
			<xsl:text disable-output-escaping="yes">.xml";</xsl:text>

			<xsl:choose>
				<!-- Case: no history found ie. the game hasn't started) -->
				<xsl:when test="$currentStep=1">
					<xsl:text disable-output-escaping="yes">document.body.setAttribute('onLoad', 'clock( </xsl:text>
					<xsl:value-of select="/match/startclock"/>
					<xsl:text disable-output-escaping="yes">+</xsl:text>
					<xsl:value-of select="/match/playclock"/>
					<xsl:text disable-output-escaping="yes">, </xsl:text>
					<xsl:value-of select="/match/timestamp"/>
					<xsl:text disable-output-escaping="yes">)');</xsl:text>
				</xsl:when>

				<!-- Case: history but no reward was found ie. we're in the middle of a game -->
				<xsl:when test="not(/match/scores/reward)">
					<xsl:text disable-output-escaping="yes">document.body.setAttribute('onLoad', 'clock( </xsl:text>
					<xsl:value-of select="/match/playclock"/>
					<xsl:text disable-output-escaping="yes">, </xsl:text>
					<xsl:value-of select="/match/timestamp"/>
					<xsl:text disable-output-escaping="yes">)');</xsl:text>
				</xsl:when>

				<!-- Case: a reward was found => the game is over -->
				<xsl:otherwise>
					document.body.setAttribute('onLoad', 'turnOffTimer()');
				</xsl:otherwise>

			</xsl:choose>

		</script>

		<div class="playClock">

			<span class="heading">
				<xsl:choose>
					<xsl:when test="$currentStep=1">Start Clock:</xsl:when>
					<xsl:otherwise>Play Clock:</xsl:otherwise>
				</xsl:choose>
			</span>
			<div class="underline"/>

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
