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

@Grab('org.jetbrains.intellij:plugin-repository-rest-client:2.0.50')
@Grab('org.slf4j:slf4j-simple:2.0.16')

import org.jetbrains.intellij.pluginRepository.PluginRepository
import org.jetbrains.intellij.pluginRepository.PluginRepositoryException
import org.jetbrains.intellij.pluginRepository.PluginRepositoryFactory
import org.jetbrains.intellij.pluginRepository.model.PluginVendorBean

String pluginUrl = System.getenv('JETBRAINS_PLUGIN_URL') ?: 'https://plugins.jetbrains.com'
String pluginToken = System.getenv('JETBRAINS_PLUGIN_TOKEN')

if (pluginToken == null || pluginToken.trim().isEmpty()) {
    println "ERROR: Missing JETBRAINS_PLUGIN_TOKEN environment variable"
    println ""
    println "Usage:"
    println "  export JETBRAINS_PLUGIN_TOKEN='your-token-here'"
    println "  groovy test-jetbrains-plugin-repo-validation.groovy"
    println ""
    println "FAILURE|ERROR:Missing JETBRAINS_PLUGIN_TOKEN environment variable"
    System.exit(1)
}

println "Testing JetBrains Plugin Repository connection..."
println "URL: ${pluginUrl}"
println "Token: ${pluginToken ? '***' + pluginToken.takeRight(4) : 'NOT SET'}"
println ""

try {
    PluginRepository repository = PluginRepositoryFactory.create(pluginUrl, pluginToken)
    PluginVendorBean vendor = repository.getVendorManager().getVendorOfCurrentUser()
    
    if (vendor == null) {
        throw new PluginRepositoryException('Authenticated but no vendor is associated with this token')
    }
    
    String vendorName = vendor.getPublicName() ?: vendor.getName()
    String vendorUrl = vendor.getUrl() ?: 'N/A'
    
    println "✅ SUCCESS: Credentials valid"
    println "Vendor Name: ${vendorName}"
    println "Vendor URL: ${vendorUrl}"
    println ""
    println "SUCCESS|VENDOR_NAME:${vendorName}|VENDOR_URL:${vendorUrl}"
    
} catch (PluginRepositoryException ex) {
    println "❌ FAILURE: ${ex.message}"
    println ""
    println "FAILURE|ERROR:${ex.message}"
    System.exit(1)
} catch (Exception ex) {
    println "❌ ERROR: ${ex.message}"
    ex.printStackTrace()
    println ""
    println "FAILURE|ERROR:${ex.message}"
    System.exit(1)
}


