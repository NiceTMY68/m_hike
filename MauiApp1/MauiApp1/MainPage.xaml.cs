using MauiApp1.Pages;

namespace MauiApp1;

public partial class MainPage : ContentPage
{
    public MainPage()
    {
        InitializeComponent();
    }

    private async void OnAddHikeClicked(object? sender, EventArgs e)
    {
        await Navigation.PushAsync(new AddEditHikePage());
    }

    private async void OnViewHikesClicked(object? sender, EventArgs e)
    {
        await Navigation.PushAsync(new ViewHikesPage());
    }
}
