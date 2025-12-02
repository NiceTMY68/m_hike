using SQLite;

namespace MauiApp1.Models;

[Table("hikes")]
public class Hike
{
    [PrimaryKey, AutoIncrement, Column("id")]
    public long Id { get; set; }

    [Column("name"), NotNull]
    public string Name { get; set; } = string.Empty;

    [Column("location"), NotNull]
    public string Location { get; set; } = string.Empty;

    [Column("date"), NotNull]
    public string Date { get; set; } = string.Empty;

    [Column("parking_available"), NotNull]
    public string ParkingAvailable { get; set; } = string.Empty;

    [Column("length"), NotNull]
    public string Length { get; set; } = string.Empty;

    [Column("difficulty"), NotNull]
    public string Difficulty { get; set; } = string.Empty;

    [Column("description")]
    public string? Description { get; set; }

    [Column("weather")]
    public string? Weather { get; set; }

    [Column("estimated_duration")]
    public string? EstimatedDuration { get; set; }

    public override string ToString()
    {
        return $"{Name} - {Location} ({Date})";
    }
}

