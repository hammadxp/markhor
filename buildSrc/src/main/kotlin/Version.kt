sealed class Version(val major: Int, val minor: Int, val patch: Int, val build: Int = 0) {
    abstract val name: String
    abstract val code: Long

    class Alpha(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int) :
        Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override val name: String
            get() = "${major}.${minor}.${patch}-alpha.$build"

        override val code: Long
            get() = major * MAJOR + minor * MINOR + patch * PATCH + build * BUILD + ALPHA
    }

    class Beta(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int) :
        Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override val name: String
            get() = "${major}.${minor}.${patch}-beta.$build"

        override val code: Long
            get() = major * MAJOR + minor * MINOR + patch * PATCH + build * BUILD + BETA
    }

    class Stable(versionMajor: Int, versionMinor: Int, versionPatch: Int) :
        Version(versionMajor, versionMinor, versionPatch) {
        override val name: String
            get() = "${major}.${minor}.${patch}"

        override val code: Long
            get() = major * MAJOR + minor * MINOR + patch * PATCH + build * BUILD + STABLE
    }

    class ReleaseCandidate(
        versionMajor: Int,
        versionMinor: Int,
        versionPatch: Int,
        versionBuild: Int,
    ) : Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override val name: String
            get() = "${major}.${minor}.${patch}-rc.$build"

        override val code: Long
            get() =
                major * MAJOR + minor * MINOR + patch * PATCH + build * BUILD + RELEASE_CANDIDATE
    }
}

// private const val ABI = 1L
private const val BUILD = 10L
private const val VARIANT = 100L
private const val PATCH = 10_000L
private const val MINOR = 1_000_000L
private const val MAJOR = 100_000_000L

private const val STABLE = VARIANT * 4
private const val ALPHA = VARIANT * 1
private const val BETA = VARIANT * 2
private const val RELEASE_CANDIDATE = VARIANT * 3

// INFO: There is no hardcoded version. The release workflow passes the next version as
// -PappVersion, and local builds fall back to the latest git tag (see app/build.gradle.kts).
const val FALLBACK_VERSION = "1.0.0"

private val versionPattern = Regex("""^v?(\d+)\.(\d+)\.(\d+)(?:-(alpha|beta|rc)\.(\d+))?$""")

/**
 * Parses a version name or tag into a [Version].
 *
 * Examples: "v1.2.3" -> Stable(1, 2, 3), "1.3.0-beta.2" -> Beta(1, 3, 0, 2)
 */
fun parseVersion(text: String): Version {
    val match =
        requireNotNull(versionPattern.matchEntire(text.trim())) {
            "Invalid version \"$text\". Use MAJOR.MINOR.PATCH, optionally with -alpha.N, -beta.N or -rc.N."
        }
    val (major, minor, patch, label, build) = match.destructured
    return when (label) {
        "alpha" -> Version.Alpha(major.toInt(), minor.toInt(), patch.toInt(), build.toInt())
        "beta" -> Version.Beta(major.toInt(), minor.toInt(), patch.toInt(), build.toInt())
        "rc" -> Version.ReleaseCandidate(major.toInt(), minor.toInt(), patch.toInt(), build.toInt())
        else -> Version.Stable(major.toInt(), minor.toInt(), patch.toInt())
    }
}
