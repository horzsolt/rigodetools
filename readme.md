Latest comments:
https://github.com/horzsolt/rigodetools/blob/master/src/main/java/horzsolt/rigodetools/mp3/Mp3Getter.java#L69
[optional] use Objects::nonNull

https://github.com/horzsolt/rigodetools/blob/master/src/main/java/horzsolt/rigodetools/mp3/Mp3Getter.java#L64
[must fix] 'return' and '{ }' is not needed

https://github.com/horzsolt/rigodetools/blob/master/src/main/java/horzsolt/rigodetools/mp3/Mp3Getter.java#L36
[codesmell] state in singleton bean

https://github.com/horzsolt/rigodetools/blob/master/src/main/java/horzsolt/rigodetools/mp3/Mp3Getter.java#L36
[must fix] use emptyList as default value instead of null that way the code will be much better in FileToMp3Mapper. You can avoid null checks (e.g.: FileToMp3Mapper.java#L119 and FileToMp3Mapper.java#L87)

https://github.com/horzsolt/rigodetools/blob/master/src/main/java/horzsolt/rigodetools/mp3/FileToMp3Mapper.java#L25
[must fix] either @Component or utility class with static methods not both

https://github.com/horzsolt/rigodetools/blob/master/src/main/java/horzsolt/rigodetools/mp3/Mp3Getter.java#L63
[optional] .map("/MP3/0-DAY/"::concat)

https://github.com/horzsolt/rigodetools/blob/master/src/main/java/horzsolt/rigodetools/mp3/Mp3Getter.java#L46
[question] is this line does anything?