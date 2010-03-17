package tud.ggpserver.ratingsystem;

/**
 *
 * Need to produce some chart prior to this action call in a Java bean
 * Need a session attribute named "chartImage";
 *
 */

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.keypoint.PngEncoder;

public class ChartViewer extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5978245452221586225L;

	public void init() throws ServletException {
	}

	//Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		// get the chart from session
		HttpSession session = request.getSession();
		BufferedImage chartImage = (BufferedImage) session.getAttribute("chartImage");

		// set the content type so the browser can see this as a picture
		response.setContentType("image/png");

		// send the picture
		PngEncoder encoder = new PngEncoder(chartImage, false, 0, 9);
		response.getOutputStream().write(encoder.pngEncode());

	}

	//Process the HTTP Post request
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}

	//Process the HTTP Put request
	public void doPut(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	}

	//Clean up resources
	public void destroy() {
	}

}