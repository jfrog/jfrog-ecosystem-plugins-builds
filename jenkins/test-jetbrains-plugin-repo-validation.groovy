#!/usr/bin/env groovy

/**
 * Local test script for JetBrains Plugin Repository token validation
 * 
 * Usage:
 *   1. Set your token as an environment variable:
 *      export JETBRAINS_PLUGIN_TOKEN="your-token-here"
 *   
 *   2. Run the script:
 *      groovy test-jetbrains-plugin-repo-validation.groovy
 *   
 *   Or specify URL (optional, defaults to https://plugins.jetbrains.com):
 *      export JETBRAINS_PLUGIN_URL="https://plugins.jetbrains.com"
 *      groovy test-jetbrains-plugin-repo-validation.groovy
 * 
 * Requirements:
 *   - Groovy installed (check with: groovy --version)
 *   - Internet access for @Grab to download dependencies
 */

// @Grab annotations must appear before any executable code
@Grab('org.jetbrains.intellij:plugin-repository-rest-client:2.0.50')
@Grab('org.slf4j:slf4j-simple:2.0.16')

import org.jetbrains.intellij.pluginRepository.PluginRepository
import org.jetbrains.intellij.pluginRepository.PluginRepositoryException
import org.jetbrains.intellij.pluginRepository.PluginRepositoryFactory
import org.jetbrains.intellij.pluginRepository.model.PluginVendorBean

// Force unbuffered output and verify script is executing
System.out.flush()
System.err.flush()
System.out.println "Script started"
System.out.flush()

System.out.println "Dependencies loaded"
System.out.flush()

String pluginUrl = System.getenv('JETBRAINS_PLUGIN_URL') ?: 'https://plugins.jetbrains.com'
String pluginToken = System.getenv('JETBRAINS_PLUGIN_TOKEN')

// Force output flushing to ensure output is visible
System.out.flush()
System.err.flush()

if (pluginToken == null || pluginToken.trim().isEmpty()) {
    System.out.println "ERROR: Missing JETBRAINS_PLUGIN_TOKEN environment variable"
    System.out.println ""
    System.out.println "Usage:"
    System.out.println "  export JETBRAINS_PLUGIN_TOKEN='your-token-here'"
    System.out.println "  groovy test-jetbrains-plugin-repo-validation.groovy"
    System.out.println ""
    System.out.println "FAILURE|ERROR:Missing JETBRAINS_PLUGIN_TOKEN environment variable"
    System.out.flush()
    System.exit(1)
}

System.out.println "Testing JetBrains Plugin Repository connection..."
System.out.println "URL: ${pluginUrl}"
System.out.println "Token: ${pluginToken ? '***' + pluginToken.takeRight(4) : 'NOT SET'}"
System.out.println ""
System.out.flush()

try {
    PluginRepository repository = PluginRepositoryFactory.create(pluginUrl, pluginToken)
    PluginVendorBean vendor = repository.getVendorManager().getVendorOfCurrentUser()
    
    if (vendor == null) {
        throw new PluginRepositoryException('Authenticated but no vendor is associated with this token')
    }
    
    String vendorName = vendor.getPublicName() ?: vendor.getName()
    String vendorUrl = vendor.getUrl() ?: 'N/A'
    
    System.out.println "✅ SUCCESS: Credentials valid"
    System.out.println "Vendor Name: ${vendorName}"
    System.out.println "Vendor URL: ${vendorUrl}"
    System.out.println ""
    System.out.println "SUCCESS|VENDOR_NAME:${vendorName}|VENDOR_URL:${vendorUrl}"
    System.out.flush()
    
    // Explicitly exit with success code
    System.exit(0)
    
} catch (PluginRepositoryException ex) {
    System.err.println "❌ FAILURE: ${ex.message}"
    System.err.println ""
    System.out.println "FAILURE|ERROR:${ex.message}"
    System.out.flush()
    System.err.flush()
    System.exit(1)
} catch (Exception ex) {
    System.err.println "❌ ERROR: ${ex.message}"
    ex.printStackTrace()
    System.err.println ""
    System.out.println "FAILURE|ERROR:${ex.message}"
    System.out.flush()
    System.err.flush()
    System.exit(1)
}


