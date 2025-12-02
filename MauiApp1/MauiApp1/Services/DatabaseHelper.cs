using SQLite;
using MauiApp1.Models;

namespace MauiApp1.Services;

public class DatabaseHelper
{
    private readonly SQLiteAsyncConnection _database;
    private const string DatabaseName = "m_hike.db";

    public DatabaseHelper()
    {
        var databasePath = Path.Combine(FileSystem.AppDataDirectory, DatabaseName);
        _database = new SQLiteAsyncConnection(databasePath);
        _ = InitializeDatabaseAsync();
    }

    private async Task InitializeDatabaseAsync()
    {
        await _database.CreateTableAsync<Hike>();
    }

    // Hike CRUD operations
    public async Task<long> AddHikeAsync(Hike hike)
    {
        return await _database.InsertAsync(hike);
    }

    public async Task<List<Hike>> GetAllHikesAsync()
    {
        return await _database.Table<Hike>()
            .OrderByDescending(h => h.Date)
            .ToListAsync();
    }

    public async Task<Hike?> GetHikeAsync(long id)
    {
        return await _database.Table<Hike>()
            .Where(h => h.Id == id)
            .FirstOrDefaultAsync();
    }

    public async Task<int> UpdateHikeAsync(Hike hike)
    {
        return await _database.UpdateAsync(hike);
    }

    public async Task<int> DeleteHikeAsync(long id)
    {
        return await _database.DeleteAsync<Hike>(id);
    }

    public async Task<int> DeleteAllHikesAsync()
    {
        return await _database.DeleteAllAsync<Hike>();
    }
}

