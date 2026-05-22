/**
 * Classpath-resource-based repository implementations that load game assets (music tracks, sound effects) directly from
 * the application JAR or the build output directory.
 *
 * <p>
 * These repositories do not require a database connection; they resolve assets via {@link java.lang.Class#getResource}
 * and the {@link java.nio.file.FileSystems} API so that they work both on a regular filesystem and inside an executable
 * JAR.
 */
package ulb.repositories.resource;
