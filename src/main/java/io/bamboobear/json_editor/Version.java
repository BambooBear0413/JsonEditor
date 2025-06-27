package io.bamboobear.json_editor;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version implements Comparable<Version>{
	public final int major;
	public final int minor;
	public final int patch;
	
	public final PreReleaseType preReleaseType;
	public final int preReleaseNumber;

	private static final Pattern PATTERN = Pattern.compile("^(?<major>0|[1-9][0-9]*)\\.(?<minor>0|[1-9][0-9]*)(?:\\.(?<patch>0|[1-9][0-9]*))?(?:-(?<name>alpha|beta|pre|rc)\\.?(?<number>[1-9][0-9]*))?$");
	
	private Version(int major, int minor, int patch, PreReleaseType preReleaseType, int preReleaseNumber) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.preReleaseType = preReleaseType;
		this.preReleaseNumber = preReleaseNumber;
	}
	
	private static int checkNumber(int i, boolean canBeZero, String name) {
		if(i < 0) throw new IllegalArgumentException(name + " is a negative number");
		if(!canBeZero && i == 0) throw new IllegalArgumentException(name + " is zero");
		return i;
	}
	
	@Override
	public int compareTo(Version obj) {
		if(this == obj) return 0;
		
		if(this.major != obj.major) return this.major - obj.minor;
		if(this.minor != obj.minor) return this.minor - obj.minor;
		if(this.patch != obj.patch) return this.patch - obj.patch;
		
		if(this.preReleaseType == null && obj.preReleaseType == null) return 0;
		if(this.preReleaseType == null) return 1;
		if( obj.preReleaseType == null) return -1;
		
		if(this.preReleaseType != obj.preReleaseType) return this.preReleaseType.compareTo(obj.preReleaseType);
		if(this.preReleaseNumber != obj.preReleaseNumber) return this.preReleaseNumber - obj.preReleaseNumber;
		return 0;
	}
	
	@Override
	public int hashCode() {
		int result = major * 95 + minor * 63 + patch * 32;
		if(preReleaseType == null) return result;
		return result + preReleaseType.hashCode() + preReleaseNumber;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Version version)) return false;
		return this.compareTo(version) == 0;
	}
	
	private String toStringWithoutPreRelease() {
		return major + "." + minor + "." + patch;
	}
	
	public String toSimpleString() {
		if(preReleaseType == null) return toStringWithoutPreRelease();
		return toStringWithoutPreRelease() + "-" + preReleaseType.simpleName + "." + preReleaseNumber;
	}
	
	@Override
	public String toString() {
		if(preReleaseType == null) return toStringWithoutPreRelease();
		return toStringWithoutPreRelease() + "\s" + preReleaseType.fullName + "\s" + preReleaseNumber;
	}
	
	public static VersionBuilder builder(int major) {
		return new VersionBuilder(major, 0, 0);
	}
	
	public static VersionBuilder builder(int major, int minor) {
		return new VersionBuilder(major, minor, 0);
	}
	
	public static VersionBuilder builder(int major, int minor, int patch) {
		return new VersionBuilder(major, minor, patch);
	}

	public static Version parse(String string) {
		Matcher matcher = PATTERN.matcher(string);
		if(!matcher.find()) throw new IllegalArgumentException("Illegal version format: " + string);

		/* throws NumberFormatException if it is not an "int" */
		int major = Integer.parseInt(matcher.group("major"));
		int minor = Integer.parseInt(matcher.group("minor"));
		int patch = Integer.parseInt(Objects.requireNonNull(matcher.group("patch"), "0"));

		VersionBuilder builder = builder(major, minor, patch);
		
		String type = matcher.group("name");
		if(type == null) return builder.build();
		int number = Integer.parseInt(Objects.requireNonNullElse(matcher.group("number"), "0"));

		return switch(PreReleaseType.getFromSimpleName(type)) {
			case ALPHA -> builder.alpha(number);
			case BETA -> builder.beta(number);
			case PRE_RELEASE -> builder.preRelease(number);
			case RELEASE_CANDIDATE -> builder.releaseCandidate(number);
		};
	}
	
	public enum PreReleaseType {
		ALPHA("Alpha", "alpha"),
		BETA("Beta", "beta"),
		PRE_RELEASE("Pre-Release", "pre"),
		RELEASE_CANDIDATE("Release Candidate", "rc");
		
		private final String fullName;
		private final String simpleName;
		
		private PreReleaseType(String full, String simple) {
			this.fullName = full;
			this.simpleName = simple;
		}

		public static PreReleaseType getFromSimpleName(String name) {
			return switch(name) {
				case "alpha" -> ALPHA;
				case "beta"  -> BETA;
				case "pre"   -> PRE_RELEASE;
				case "rc"    -> RELEASE_CANDIDATE;
				default -> throw new IllegalArgumentException("Unknown pre-realease type");
			};
		}
	}
	
	public static class VersionBuilder {
		private final int major;
		private final int minor;
		private final int patch;
		
		private VersionBuilder(int major, int minor, int patch) {
			this.major = checkNumber(major, true, "major");
			this.minor = checkNumber(minor, true, "minor");
			this.patch = checkNumber(patch, true, "patch");
		}
		
		public Version build() {
			return new Version(major, minor, patch, null, 0);
		}
		
		public Version alpha(int i) {
			checkNumber(i, false, "pre-release number");
			return new Version(major, minor, patch, PreReleaseType.ALPHA, i);
		}
		
		public Version beta(int i) {
			checkNumber(i, false, "pre-release number");
			return new Version(major, minor, patch, PreReleaseType.BETA, i);
		}
		
		public Version preRelease(int i) {
			checkNumber(i, false, "pre-release number");
			return new Version(major, minor, patch, PreReleaseType.PRE_RELEASE, i);
		}
		
		public Version releaseCandidate(int i) {
			checkNumber(i, false, "pre-release number");
			return new Version(major, minor, patch, PreReleaseType.RELEASE_CANDIDATE, i);
		}
	}
}
