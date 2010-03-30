<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing the generic game-master header.
	- For use within <body>.
	- needs css/main.css and sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="header">
		
		<xsl:variable name="currentStep" select="count(/match/history/step)+1"/>
		<xsl:variable name="role" select="/match/sight-of"/>
		<xsl:variable name="playing">
			<xsl:choose>
				<xsl:when test="count(/match/legalmoves) = 1">1</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- play mode -->
		<xsl:if test="not(/match/scores/reward)">
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
					var timerID;
					HTTP_GET_VARS=new Array();
					strGET=document.location.search.substr(1,document.location.search.length);
					if(strGET!='')
					    {
					    gArr=strGET.split('&');
					    for(i=0;i<gArr.length;++i)
					        {
					        v='';vArr=gArr[i].split('=');
					        if(vArr.length>1){v=vArr[1];}
					        HTTP_GET_VARS[unescape(vArr[0])]=unescape(v);
					        }
					    }
					
					function GET(v)
					{
					if(!HTTP_GET_VARS[v]){return 'undefined';}
					return HTTP_GET_VARS[v];
					}
					
					if ( GET('seconds') != 'undefined' ) {
						timerID = setTimeout("document.location.replace(page);", GET('seconds')*1000);
					}
				]]>
				// --&gt;
			</script>
		</xsl:if>
		
		<div class="header" id="header">
			
			<span class="heading">Match:</span><span class="content"><xsl:value-of select="/match/match-id"/></span>, 
			<span class="heading">Step:</span><span class="content"><xsl:value-of select="$currentStep"/></span>, 
			<!-- <select name="seconds" id="seconds">
			  <option value="1">1</option>
			  <option value="2">2</option>
			  <option value="3">3</option>
			  <option value="5">5</option>
			  <option value="10">10</option>
			</select> -->
			<br/>
			
			<xsl:choose>
				<xsl:when test="$role='RANDOM'">
					<!--  <span class="heading">Complete Information</span> -->
				</xsl:when>
				<xsl:otherwise>
					<span class="heading">Seen by:</span><span class="content"><xsl:value-of select="$role"/></span>
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="$playing = 0">
					
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">initial</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">previous</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">play</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">pause</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">next</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">final</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					
				</xsl:when>
			</xsl:choose>
			
			<div class="underline" style="clear:left;"/>
			
		</div>
		
		<xsl:choose>
			<xsl:when test="$playing = 0">
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
			</xsl:when>
		</xsl:choose>
		
	</xsl:template>



	<xsl:template name="make_tab">
		<xsl:param name="which"/>
		<xsl:param name="currentStep"/>
		<xsl:param name="role"/>

		<div class="bartab">
			<a>
				
				<xsl:attribute name="id">navigation_<xsl:value-of select="$which"/></xsl:attribute>
				
				<xsl:variable name="linkStep">
					<xsl:choose>
						<xsl:when test="$which='initial'">1</xsl:when>
						<xsl:when test="$which='previous'"><xsl:value-of select="$currentStep - 1"/></xsl:when>
						<xsl:when test="$which='play'"><xsl:value-of select="$currentStep + 1"/></xsl:when>
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
				
				<xsl:if test="$which = 'play'">
					<xsl:attribute name="href">
						javascript:document.location.replace("<xsl:call-template name="makeStepLinkURL">
							<xsl:with-param name="step" select="$linkStep"/>
							<xsl:with-param name="role" select="$role"/>
							<xsl:with-param name="seconds" select="1"/>
						</xsl:call-template>");
					</xsl:attribute>
				</xsl:if>
				
				<xsl:if test="$which = 'pause'">
					<xsl:attribute name="href">
						javascript:clearTimeout(timerID); document.location.replace("<xsl:call-template name="makeStepLinkURL">
							<xsl:with-param name="step" select="$currentStep"/>
							<xsl:with-param name="role" select="$role"/>
						</xsl:call-template>");
					</xsl:attribute>
				</xsl:if>
				
				<xsl:attribute name="title">
					<xsl:choose>
						<xsl:when test="$which='initial'">initial state</xsl:when>
						<xsl:when test="$which='play'">play</xsl:when>
						<xsl:when test="$which='pause'">pause</xsl:when>
						<xsl:when test="$which='final'">final state</xsl:when>
						<xsl:otherwise>state <xsl:value-of select="$linkStep"/></xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>

				<xsl:variable name="imageName">
					<xsl:choose>
						<xsl:when test="$which='initial'">gnome-go-first</xsl:when>
						<xsl:when test="$which='previous'">gnome-go-previous</xsl:when>
						<xsl:when test="$which='play'">play</xsl:when>
						<xsl:when test="$which='pause'">pause</xsl:when>
						<xsl:when test="$which='next'">gnome-go-next</xsl:when>
						<xsl:when test="$which='final'">gnome-go-last</xsl:when>
					</xsl:choose>
				</xsl:variable>

				<img width="30" height="30">
					<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>/generic/images/<xsl:value-of select="normalize-space($imageName)"/>.png</xsl:attribute>
				</img>
			</a>
		</div>
	</xsl:template>

</xsl:stylesheet>