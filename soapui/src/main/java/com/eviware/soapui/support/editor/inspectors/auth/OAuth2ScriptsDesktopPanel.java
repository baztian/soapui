package com.eviware.soapui.support.editor.inspectors.auth;

import com.eviware.soapui.impl.rest.OAuth2Profile;
import com.eviware.soapui.ui.support.DefaultDesktopPanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * A simple desktop panel wrapping the OAuth2ScriptsEditor component.
 * @see OAuth2ScriptsEditor
 */
public class OAuth2ScriptsDesktopPanel extends DefaultDesktopPanel
{
	public OAuth2ScriptsDesktopPanel( OAuth2Profile profile )
	{
		super( "Automation scripts for OAuth2 profile",
				"A panel used to edit JavaScript fragments that automate user interactions in an OAuth2 flow",
				new JPanel(new BorderLayout(  )) );
		JPanel contentPane = (JPanel )getComponent();
		OAuth2ScriptsEditor editor = new OAuth2ScriptsEditor( profile );
		editor.setPreferredSize( new Dimension(900, 700) );
		contentPane.add( editor, BorderLayout.CENTER );
	}
}
