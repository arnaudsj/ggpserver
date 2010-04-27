<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for writing legalMoves for a HumanPlayer to the screen.
	- For use within <body>.
	- needs sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:import href="sitespecific.xsl"/> <!-- $stylesheetURL, makeStepLinkURL and makePlayLinkURL template -->
	<xsl:import href="variables.xsl"/>

	<xsl:template name="legalMoves">
		
		<xsl:if test="$playing = 1"> <!-- display something only if we are playing-->
			<div class="legalMoves">
				
				<span class="heading">Legal moves: </span>
				<div class="underline"/>
				
				<input type="checkbox" name="autoConfirm" value="auto">
					<xsl:if test="/match/quickConfirm">
						<xsl:attribute name="checked" />
					</xsl:if>
					<xsl:variable name="confirm" select="count(/match/legalmoves/move)=1"/>
					<xsl:attribute name="onclick">
							if (checked) {
								location.replace("<xsl:call-template name="makePlayLinkURL">
									<xsl:with-param name="quickConfirm">true</xsl:with-param>
									<xsl:with-param name="confirm" select="$confirm"/>
								</xsl:call-template>");
							} else {
								location.replace("<xsl:call-template name="makePlayLinkURL">
									<xsl:with-param name="quickConfirm">false</xsl:with-param>
								</xsl:call-template>");
							};
					</xsl:attribute>
				</input>
				<span title="When this box is checked, you will not be prompted when you have only one possible move, it will be automatically confirmed. This can be more pleasant if you have to play 'NOOPs' very often.">Quick confirm (?)</span>
				<br/>
				
				<xsl:choose>
					<xsl:when test="/match/chosenmove/confirmed"> <!-- reload the page in case the move has been confirmed... -->
						move confirmed: <xsl:for-each select="/match/chosenmove/move-term"><xsl:call-template name="move2text"/></xsl:for-each>
						<br/>
						<xsl:call-template name="loadingImg" />
						<script language="JavaScript" type="text/javascript">
							<![CDATA[
								setTimeout("document.location.reload();", 2500);
							]]>
						</script>
					</xsl:when>
					<xsl:when test="count(/match/legalmoves/move) = 0"> <!-- reload the page if we don't have the legalMoves yet -->
						waiting for next state
						<br/>
						<xsl:call-template name="loadingImg" />
						<script language="JavaScript" type="text/javascript">
							<![CDATA[
								setTimeout("document.location.reload();", 2500);
							]]>
						</script>
					</xsl:when>
					<xsl:otherwise>
						<span class="heading">Confirm move: </span>
						<span class="content">
							<a>
								<xsl:attribute name="href">
									javascript:location.replace("
									<xsl:call-template name="makePlayLinkURL">
										<xsl:with-param name="confirm" select="'true'"/>
									</xsl:call-template>
									")
								</xsl:attribute>
									<xsl:for-each select="/match/chosenmove/move-term"><xsl:call-template name="move2text"/></xsl:for-each>
							</a>
						</span>
						<br/>
						<div style="overflow: auto; max-height: 420px; overflow-x: hidden;">
							<style type="text/css" media="all">
								td:last-child {padding-right: 20px;} /*prevent Mozilla scrollbar from hiding cell content*/
							</style>
							<table>
								<tbody>
									<xsl:for-each select="/match/legalmoves/move">
										<xsl:sort select="move-term"/>
										<tr><td>
											<xsl:call-template name="legalMove">
												<xsl:with-param name="url">
													<xsl:call-template name="makePlayLinkURL">
														<xsl:with-param name="chosenMove" select="./move-number"/>
													</xsl:call-template>
												</xsl:with-param>
											</xsl:call-template>
										</td></tr>
									</xsl:for-each>
								</tbody>
							</table>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>

	</xsl:template>
	
	<xsl:template name="makePlayLinkURL">
		<xsl:param name="chosenMove" /> <!-- the move-number of the chosen move (0 <= chosenMove < count(/match/legalmoves/move)) -->
		<xsl:param name="confirm" /> <!-- true for confirm, false or '' for just selecting the move -->
		<xsl:param name="quickConfirm" /> <!-- true to enable quickConfirm, false to disable it -->


		<xsl:text disable-output-escaping="yes">play.jsp?matchID=</xsl:text>
		<xsl:value-of select="/match/match-id" />
		<xsl:text disable-output-escaping="yes">&amp;role=</xsl:text>
		<xsl:value-of select="/match/sight-of" />
		<xsl:text disable-output-escaping="yes">&amp;forStepNumber=</xsl:text>
		<xsl:value-of select="count(/match/history/step)+1" />
		<xsl:if test="$chosenMove != ''">
			<xsl:text disable-output-escaping="yes">&amp;chosenMove=</xsl:text>
			<xsl:value-of select="$chosenMove" />
		</xsl:if>
		<xsl:if test="$confirm != ''">
			<xsl:text disable-output-escaping="yes">&amp;confirm=</xsl:text>
			<xsl:value-of select="$confirm"/>
		</xsl:if>
		<xsl:if test="$quickConfirm != ''">
			<xsl:text disable-output-escaping="yes">&amp;quickConfirm=</xsl:text>
			<xsl:value-of select="$quickConfirm"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="loadingImg">
		<img width="16" height="16" title="waiting...">
			<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>/generic/images/16-loading.gif</xsl:attribute>
			loading...
		</img>
	</xsl:template>
	
	<xsl:template name="move2text">
		<xsl:if test="count(arg)>0">(</xsl:if>
		<xsl:value-of select="prop-f"/>
		<xsl:for-each select="arg">&#160;<xsl:value-of select="."/></xsl:for-each>
		<xsl:if test="count(arg)>0">)</xsl:if>
	</xsl:template>
	
	<xsl:template name="legalMove">
		<xsl:param name="url"/> <!-- the URL to propose this move -->
		
		<span class="content">
			<a>
				<xsl:attribute name="href">javascript:location.replace("<xsl:value-of select="$url" />");</xsl:attribute>
				<xsl:for-each select="./move-term"> <xsl:call-template name="move2text"/> </xsl:for-each>
			</a>
		</span>
	</xsl:template>

</xsl:stylesheet>

