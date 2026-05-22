/**
 * Application bootstrap layer responsible for wiring infrastructure objects together at startup.
 *
 * <p>
 * The two classes in this package form the composition root of the application:
 * <ul>
 * <li>{@link ulb.bootstrap.GameBootstrapper} — opens the database, parses static data, instantiates all repositories
 * and services, and creates or resumes a player session.</li>
 * <li>{@link ulb.bootstrap.ServiceRegistry} — a thin value object that bundles every service instance so controllers
 * can receive them in a single constructor argument.</li>
 * </ul>
 *
 * <p>
 * Nothing outside this package should create service or repository instances directly.
 */
package ulb.bootstrap;
