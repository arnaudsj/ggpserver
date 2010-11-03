<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing the generic game-master header.
	- For use within <body>.
	- needs css/main.css and sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="variables.xsl"/>

	<xsl:template name="header">
		<div class="header" id="header">
			<xsl:call-template name="print_match_info"/>
			<xsl:call-template name="make_navigation_links"/>
			<div class="underline" style="clear:left;"/>
		</div>
	</xsl:template>

	<xsl:template name="print_match_info">
		<span class="heading">Match:</span><span class="content"><xsl:value-of select="/match/match-id"/></span>, 
		<span class="heading">Step:</span><span class="content"><xsl:value-of select="$currentStep"/></span>,
		<span class="heading">Seen by:</span><span class="content">
			<script language="JavaScript" type="text/javascript">
				// &lt;!--
						function change_viewpoint(role) {
							newLocation = "<xsl:call-template name="makeStepLinkURL"><xsl:with-param name="step" select="$currentStep"/><xsl:with-param name="role" select="'THEROLE'"/></xsl:call-template>";
							newLocation = newLocation.replace("THEROLE", role);
							location.replace(newLocation);
						}
				// --&gt;
			</script>
			<select name="view_of_role" size="1" onclick="javascript:change_viewpoint(this.value)">
				<xsl:for-each select="match/role">
					<xsl:choose>
						<xsl:when test="translate($role, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ') = translate(., 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')">
							<option selected="selected">
								<xsl:value-of select="translate(., 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
							</option>
						</xsl:when>
						<xsl:otherwise>
							<option>
								<xsl:value-of select="translate(., 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
							</option>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</select>
		</span>
		<br/>
	</xsl:template>

	<xsl:template name="make_navigation_links">

		<xsl:call-template name="make_tab">
			<xsl:with-param name="which">initial</xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="make_tab">
			<xsl:with-param name="which">previous</xsl:with-param>
		</xsl:call-template>

		<xsl:call-template name="autoPlay"/>
		
		<xsl:call-template name="make_tab">
			<xsl:with-param name="which">next</xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="make_tab">
			<xsl:with-param name="which">final</xsl:with-param>
		</xsl:call-template>

		<script language="JavaScript" type="text/javascript">
			// &lt;!--
			<![CDATA[
					// replace links by javascript
					var link_ids = new Array("navigation_initial", "navigation_previous", "navigation_next", "navigation_final");
					for(i=0; i<link_ids.length; i++) {
						if(document.links[link_ids[i]] != null) {
							document.links[link_ids[i]].href = 'javascript:location.replace("' + document.links[link_ids[i]].href + '")';
						}
					}
			]]>
			// --&gt;
		</script>
	</xsl:template>

	<xsl:template name="make_tab">
		<xsl:param name="which"/>

		<div class="bartab">
			<a>
				
				<xsl:attribute name="id">navigation_<xsl:value-of select="$which"/></xsl:attribute>
				
				<xsl:variable name="linkStep">
					<xsl:choose>
						<xsl:when test="$which='initial'">1</xsl:when>
						<xsl:when test="$which='previous'"><xsl:value-of select="$currentStep - 1"/></xsl:when>
						<xsl:when test="$which='next'"><xsl:value-of select="$currentStep + 1"/></xsl:when>
						<xsl:when test="$which='final'">final</xsl:when>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:if test="(($which='initial' or $which='previous') and $currentStep != 1) or (($which='final' or $which='next') and not (/match/scores/reward))">
					<xsl:attribute name="href">
						<xsl:call-template name="makeStepLinkURL">
							<xsl:with-param name="step" select="$linkStep"/>
							<xsl:with-param name="role" select="$role"/>
						</xsl:call-template>
					</xsl:attribute>
				</xsl:if>
				
				<xsl:if test="$which = 'play' and not (/match/scores/reward)">
					<xsl:attribute name="href">javascript:togglePlay();</xsl:attribute>
				</xsl:if>
				
				<xsl:attribute name="title">
					<xsl:choose>
						<xsl:when test="$which='initial'">initial state</xsl:when>
						<xsl:when test="$which='play'">auto play</xsl:when>
						<xsl:when test="$which='final'">final state</xsl:when>
						<xsl:otherwise>state <xsl:value-of select="$linkStep"/></xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>

				<xsl:variable name="imageName">
					<xsl:choose>
						<xsl:when test="$which='initial'">gnome-go-first</xsl:when>
						<xsl:when test="$which='previous'">gnome-go-previous</xsl:when>
						<xsl:when test="$which='play'">play</xsl:when>
						<xsl:when test="$which='next'">gnome-go-next</xsl:when>
						<xsl:when test="$which='final'">gnome-go-last</xsl:when>
					</xsl:choose>
				</xsl:variable>

				<img width="30" height="30">
					<xsl:attribute name="id">navigation_img_<xsl:value-of select="$which"/></xsl:attribute>
					<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>/generic/images/<xsl:value-of select="normalize-space($imageName)"/>.png</xsl:attribute>
				</img>
			</a>
		</div>
	</xsl:template>
	
	<xsl:template name="autoPlay">
		
		<xsl:call-template name="make_tab">
			<xsl:with-param name="which">play</xsl:with-param>
		</xsl:call-template>

		<!-- play mode -->
		<xsl:choose>
			<xsl:when test="not(/match/scores/reward)">
				<script language="JavaScript" type="text/javascript">
					<xsl:text disable-output-escaping="yes">page = "</xsl:text>
						<xsl:call-template name="makeStepLinkURL">
							<xsl:with-param name="step" select="$currentStep+1"/>
							<xsl:with-param name="role" select="$role"/>
							<xsl:with-param name="seconds" select="1"/>
						</xsl:call-template>
					<xsl:text disable-output-escaping="yes">";</xsl:text> 
					// &lt;!--
					<![CDATA[
						var timerID = null;
						var HTTP_GET_VARS = new Array();
						var strGET = document.location.search.substr(1,document.location.search.length);
						if(strGET!='') {
							gArr=strGET.split('&');
							for(i=0;i<gArr.length;++i) {
								v='';vArr=gArr[i].split('=');
								if(vArr.length>1) { v=vArr[1]; }
								HTTP_GET_VARS[unescape(vArr[0])]=unescape(v);
							}
						}
						
						function GET(v) {
							if(!HTTP_GET_VARS[v]){return 'undefined';}
							return HTTP_GET_VARS[v];
						}
																	
						function togglePlay() {
					]]>
							<xsl:text disable-output-escaping="yes">page = "</xsl:text>
								<xsl:call-template name="makeStepLinkURL">
									<xsl:with-param name="step" select="$currentStep+1"/>
									<xsl:with-param name="role" select="$role"/>
									<xsl:with-param name="seconds" select="1"/>
								</xsl:call-template>
							<xsl:text disable-output-escaping="yes">";</xsl:text> 
					<![CDATA[
							if (document.getElementById('navigation_play').title == 'pause') {
								// state is on play (because "pause" is shown)
								if(timerID != null) {
									clearTimeout(timerID);
									timerID = null;
								}								
								document.getElementById('navigation_play').title = 'auto play';
								imgSrc = document.getElementById('navigation_img_play').src;
								imgSrc = imgSrc.replace(/pause\.png/, "play.png"); 
								document.getElementById('navigation_img_play').src = imgSrc;
							} else {
								timerID = setTimeout("document.location.replace(page);", GET('seconds')*1000);
								document.getElementById('navigation_play').title = 'pause';
								imgSrc = document.getElementById('navigation_img_play').src;
								imgSrc = imgSrc.replace(/play\.png/, "pause.png"); 
								document.getElementById('navigation_img_play').src = imgSrc;
							}
						}							

						if ( GET('seconds') != 'undefined' ) {
							togglePlay(); // state is "pause" by default so toggling will start the "auto play"
						}

					]]>
					// --&gt;
				</script>
			</xsl:when>
			<xsl:otherwise>
				<script language="JavaScript" type="text/javascript">
					// &lt;!--
					<![CDATA[
						document.getElementById('navigation_play').href = '';
					]]>
					// --&gt;
				</script>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>