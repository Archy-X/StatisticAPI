# Statistic API
StatisticAPI is a version compatible API to retrieve Minecraft statistics for Bukkit based servers, including offline players.

It works on 1.8-1.16, though it isn't needed unless you plan on supporting 1.14 or below, since OfflinePlayer statistic getting was added to the Bukkit API.

## Usage

### Maven
Add the JitPack repository:
```
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

Then add the dependency:
```
<dependency>
    <groupId>com.github.Archy-X</groupId>
    <artifactId>StatisticAPI</artifactId>
    <version>Version</version>
</dependency>
```
Version can be the first 10 characters of a commit hash or a release tag.
### Gradle
Add the repository:
```
repositories {
    maven { url 'https://jitpack.io' }
}
```
Add the dependency:
```
dependencies {
    implementation 'com.github.Archy-X:StatisticAPI:Version'
}
```
### Using the API
To use the API, simply create an instance of StatisticAPI, then call the getStatistic methods:
```java
StatisticAPI statisticAPI = new StatisticAPI();
int value = statisticAPI.getStatistic(player.getUniqueId(), Statistic.WALK_ONE_CM);
```
The first parameter is the UUID of the player, the second is a Bukkit Statistic enum.

To get statistics that require an EntityType or Material, simply add it as an additional parameter.